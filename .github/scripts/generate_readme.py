#!/usr/bin/env python3
"""
Generate README.md tables from version branches and metadata.json files.

The script:
- scans git branches matching X.Y.Z
- groups them by Minecraft version (X.Y.x)
- reads support status from versions.json
- reads loader + link data from branch metadata.json
- dynamically builds markdown tables per Minecraft version
- writes README.md

Usage:
    python generate_readme.py owner/repo

If no argument is provided, GITHUB_REPOSITORY is used.
If neither exists, README generation is skipped.
"""

import os
import json
import re
import subprocess
import sys
from pathlib import Path


DEFAULT_SUPPORT_TYPE = ("❌&nbsp;Archived", "No longer updated")
SUPPORT_TYPES = {
    "primary": ("✅&nbsp;Primary", "Latest version with active development and new features"),
    "maintained": ("✅&nbsp;Maintained", "Receives backports and selected new features"),
    "fixes": ("⚠️&nbsp;Bugfixes&nbsp;only", "Critical fixes and crash fixes only"),
    "archived": DEFAULT_SUPPORT_TYPE
}

DEFAULT_MOD_LOADER = ("", 0)
MOD_LOADERS = {
    "fabric": ("Fabric", 4),
    "forge": ("Forge", 1),
    "neoforge": ("NeoForge", 6)
}

CURSEFORGE_ICON = '<img src="https://cdn.simpleicons.org/curseforge" width="14" />&nbsp;'
MODRINTH_ICON = '<img src="https://cdn.simpleicons.org/modrinth" width="14" />&nbsp;'
DEFAULT_DOWNLOADS = (
    f'{CURSEFORGE_ICON}'
    '[CurseForge](https://www.curseforge.com/members/fuzs_/projects)<br />'
    f'{MODRINTH_ICON}'
    '[Modrinth](https://modrinth.com/user/Fuzs)'
)

MAVEN_BASE_URL = "https://github.com/Fuzss/modresources/tree/main/maven/"
README_FILE = Path("README.md")
VERSIONS_FILE = Path("versions.json")
BRANCH_PATTERN = re.compile(r"\d+\.\d+\.(\d+|x)")


def get_repo_url():
    """
    Resolve repository URL.

    Priority:
    1. first CLI argument (owner/repo)
    2. GITHUB_REPOSITORY environment variable
    3. skip generation if neither exists

    Returns:
        str: repository URL
    """
    repo = os.getenv("GITHUB_REPOSITORY")

    if len(sys.argv) > 1:
        repo = sys.argv[1]

    if not repo:
        print("GITHUB_REPOSITORY not set and no fallback provided. Skipping README generation.")
        sys.exit(0)

    return f"https://github.com/{repo}"


def format_repo_title(repo_name: str) -> str:
    """
    Convert kebab case repository names to a human readable title.

    New format
    forge-config-api-port -> Forge Config Api Port

    Old format
    forgeconfigapiport -> unchanged

    Only transform when hyphens are present to avoid modifying legacy names.
    """
    if "-" not in repo_name:
        return repo_name

    parts = repo_name.split("-")
    return " ".join(part.capitalize() for part in parts)


def load_support_data():
    """
    Load versions.json support mapping.

    Returns:
        tuple[dict[str, str], bool]: branch -> support type, published flag
    """
    if VERSIONS_FILE.exists():
        with open(VERSIONS_FILE) as f:
            data = json.load(f)
            return data.get("branches", {}), data.get("published", False)

    return {}, False


def semver_key(branch: str):
    """Convert branch 'X.Y.Z' into tuple (X,Y,Z) for proper numeric sorting."""
    return tuple(int(part) for part in branch.split(".") if part.isdigit())


def get_all_branches():
    """
    Collect all remote branches matching X.Y.Z.
    Works in GitHub Actions where only remote refs exist.
    """
    result = subprocess.run(
        ["git", "branch", "-r", "--format=%(refname:short)"],
        capture_output=True,
        text=True
    )

    branches = []

    for line in result.stdout.splitlines():
        branch = line.strip()

        if branch.startswith("origin/"):
            branch = branch[len("origin/"):]

        if BRANCH_PATTERN.fullmatch(branch):
            branches.append(branch)

    return sorted(branches, key=semver_key, reverse=True)


def group_branches_by_mc_version(branches):
    """
    Group branches into Minecraft minor versions.

    Example:
        1.21.1 -> 1.21.x
        26.1.x -> 26.x

    Returns:
        dict[str, list[str]]
    """
    versions = {}

    for branch in branches:
        components = 2 if branch.startswith("1.") else 1
        minecraft = ".".join(branch.split(".")[:components]) + ".x"
        versions.setdefault(minecraft, []).append(branch)

    return versions


def has_download_links(links):
    return any(
        link.get("name", "").lower() in {"curseforge", "modrinth"}
        for link in links
    )


def collect_table_loaders(branch_list):
    """
    Detect loaders present in metadata.json for a table without checking out branches.

    Returns:
        dict: metadata per branch
    """
    branch_metadata = {}

    for branch in branch_list:
        try:
            metadata_json_str = subprocess.run(
                ["git", "show", f"origin/{branch}:metadata.json"],
                capture_output=True,
                text=True,
                check=True
            ).stdout
        except subprocess.CalledProcessError:
            continue

        metadata = json.loads(metadata_json_str)
        branch_metadata[branch] = metadata

    return branch_metadata


def build_table_header(metadata, published):
    header_columns = ["Branch", "Status", "Changelog"]

    if metadata:
        if has_download_links(metadata.get("links", [])):
            loader_names = [
                MOD_LOADERS.get(loader, (loader.capitalize(), DEFAULT_MOD_LOADER[1]))[0]
                for loader in metadata.get("platforms", [])
            ]

            header_columns += loader_names

        if published:
            header_columns += ["Maven"]

        return header_columns
    
    else:
        return header_columns + ["Downloads"]


def get_mc_version(branch: str) -> str:
    """
    Remove trailing .0 components from branch version.

    Examples:
        26.1.0  -> 26.1
        1.20.0  -> 1.20
        1.21.1  -> 1.21.1
        1.21.10 -> 1.21.10
    """
    parts = branch.split(".")

    while parts and parts[-1] == "0" or parts[-1] == "x":
        parts.pop()

    return ".".join(parts)


def platform_links(links, minecraft, platform):
    """
    Render loader download links for a table cell.

    Returns:
        str: markdown cell content
    """
    entries = []

    for link in links:
        name = link.get("name", "").lower()
        slug = link.get("slug")

        if name == "curseforge":
            game_id = MOD_LOADERS.get(platform, DEFAULT_MOD_LOADER)[1]
            entries.append(
                f"{CURSEFORGE_ICON}"
                f"[CurseForge](https://www.curseforge.com/minecraft/mc-mods/{slug}/files/all?version={minecraft}&gameVersionTypeId={game_id})"
            )

        if name == "modrinth":
            entries.append(
                f"{MODRINTH_ICON}"
                f"[Modrinth](https://modrinth.com/mod/{slug}/versions?g={minecraft}&l={platform})"
            )

    return "<br /> ".join(entries) if entries else "n/a"


def maven_artifact(group, id, loader, version):
    display_artifact = f"{group}:{id}-{loader.lower()}:{version}"
    url_artifact = f"{group.replace(".", "/")}/{id}-{loader.lower()}/{version}"
    return f"[`{display_artifact}`]({MAVEN_BASE_URL}{url_artifact})"


def generate_table_row(
    repo_url,
    branch,
    display_status,
    changelog_url,
    metadata,
    published
):
    """
    Generate a single markdown table row.
    """
    if metadata:
        minecraft = metadata.get("minecraft") or get_mc_version(branch)
        links = metadata.get("links", [])
        branch_loaders = metadata.get("platforms", [])
        id = metadata["mod"]["id"]
        version = metadata["mod"]["version"]
        group = metadata["mod"]["group"]

        row = [
            f"[{branch}]({repo_url}/tree/{branch})",
            display_status,
            f"[CHANGELOG.md]({changelog_url})"
        ]

        if has_download_links(links):
            row += [
                platform_links(links, minecraft, loader)
                for loader in branch_loaders
            ]

        if published:
            maven_entries = [maven_artifact(group, id, "common", version)]

            for loader in branch_loaders:
                maven_entry = maven_artifact(group, id, loader, version)
                maven_entries.append(maven_entry)

            row.append("<br />".join(maven_entries))

    else:
        row = [
            f"[{branch}]({repo_url}/tree/{branch})",
            display_status,
            f"[CHANGELOG.md]({changelog_url})",
            DEFAULT_DOWNLOADS
        ]

    return "| " + " | ".join(row) + " |"


def write_readme(readme_lines):
    """Write README.md file."""
    with open(README_FILE, "w") as f:
        f.write("\n".join(readme_lines))


def main():
    repo_url = get_repo_url()
    repo_name = repo_url.split("/")[-1]

    repo_title = format_repo_title(repo_name)
    readme_lines = [f"# {repo_title}"]

    support_data, published = load_support_data()
    branches = get_all_branches()
    mc_versions = group_branches_by_mc_version(branches)

    for mc_version, branch_list in mc_versions.items():
        readme_lines.append(f"\n### Minecraft {mc_version}\n")

        branch_metadata = collect_table_loaders(branch_list)
        current_table_header = []

        for branch in branch_list:

            metadata = branch_metadata.get(branch)
            table_header = build_table_header(metadata, published)

            if table_header != current_table_header:
                if current_table_header:
                    readme_lines.append("")

                readme_lines.append("| " + " | ".join(table_header) + " |")
                readme_lines.append("| " + " | ".join(["---"] * len(table_header)) + " |")

                current_table_header = table_header

            raw_status = support_data.get(branch, "archived").lower()
            display_status = SUPPORT_TYPES.get(raw_status, DEFAULT_SUPPORT_TYPE)[0]
            changelog_url = f"{repo_url}/blob/{branch}/CHANGELOG.md"

            row = generate_table_row(
                repo_url,
                branch,
                display_status,
                changelog_url,
                metadata,
                published
            )

            readme_lines.append(row)

    readme_lines.append(f"\n---\n")
    readme_lines.extend(
        f"**{name}** — {description}\n"
        for name, description in SUPPORT_TYPES.values()
    )

    write_readme(readme_lines)


if __name__ == "__main__":
    main()
