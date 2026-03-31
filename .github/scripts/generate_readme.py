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
VERSIONS_FILE = Path(".github/scripts/versions.json")
BRANCH_PATTERN = re.compile(r"\d+\.\d+\.\d+")


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
            return data.get("versions", {}), data.get("published", False)

    return {}, False


def semver_key(branch: str):
    """Convert branch 'X.Y.Z' into tuple (X,Y,Z) for proper numeric sorting."""
    return tuple(int(x) for x in branch.split("."))


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

    Returns:
        dict[str, list[str]]
    """
    versions = {}

    for branch in branches:
        mc_version = ".".join(branch.split(".")[:2]) + ".x"
        versions.setdefault(mc_version, []).append(branch)

    return versions


def has_metadata(branch_metadata):
    return any(info for info in branch_metadata.values())


def collect_table_loaders(branch_list):
    """
    Detect loaders present in metadata.json for a table without checking out branches.

    Returns:
        tuple[list[str], dict]: loaders present, metadata per branch
    """
    loaders_present = set()
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
        loaders = metadata.get("platforms", [])

        branch_metadata[branch] = {"metadata": metadata, "loaders": loaders}
        loaders_present.update(loaders)

    return sorted(loaders_present), branch_metadata


def build_table_header(loaders_present, include_maven):
    base_columns = ["Branch", "Status", "Changelog"]

    if loaders_present:
        loader_names = [
            MOD_LOADERS.get(loader, (loader.capitalize(), DEFAULT_MOD_LOADER[1]))[0]
            for loader in loaders_present
        ]

        header = base_columns + loader_names

        if include_maven:
            header.append("Maven")

        return header

    return base_columns + ["Downloads"]


def link_url(repo_url, links, name, branch, platform=None):
    """
    Build download URL for platform.

    Returns:
        str: platform URL
    """
    for l in links:
        if l["name"].lower() == name.lower():
            slug = l.get("slug")

            if name.lower() == "curseforge":
                game_id = MOD_LOADERS.get(platform, DEFAULT_MOD_LOADER)[1]
                return (
                    "https://www.curseforge.com/minecraft/mc-mods/"
                    f"{slug}/files/all?version={branch}&gameVersionTypeId={game_id}"
                )

            if name.lower() == "modrinth":
                return f"https://modrinth.com/mod/{slug}/versions?g={branch}&l={platform}"

    return repo_url


def platform_links(repo_url, links, branch, loader, branch_loaders):
    """
    Render loader download links for a table cell.

    Returns:
        str: markdown cell content
    """
    if loader not in branch_loaders:
        return "n/a"

    entries = []

    curseforge_url = link_url(repo_url, links, "curseforge", branch, loader)
    if curseforge_url != repo_url:
        entries.append(
            f'{CURSEFORGE_ICON}'
            f'[CurseForge]({curseforge_url})'
        )

    modrinth_url = link_url(repo_url, links, "modrinth", branch, loader)
    if modrinth_url != repo_url:
        entries.append(
            f'{MODRINTH_ICON}'
            f'[Modrinth]({modrinth_url})'
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
    metadata_info,
    loader_columns,
    include_maven=True
):
    """
    Generate a single markdown table row.
    """
    if metadata_info:
        metadata = metadata_info["metadata"]
        links = metadata.get("links", [])
        branch_loaders = metadata_info["loaders"]
        id = metadata["mod"]["id"]
        version = metadata["mod"]["version"]
        group = metadata["mod"]["group"]

        row = [
            f"[{branch}]({repo_url}/tree/{branch})",
            display_status,
            f"[CHANGELOG.md]({changelog_url})"
        ]

        row += [
            platform_links(repo_url, links, branch, loader, branch_loaders)
            for loader in loader_columns
        ]

        if include_maven:
            maven_entries = [maven_artifact(group, id, "common", version)]

            for loader in metadata_info["loaders"]:
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

        current_group = []
        current_type = None

        def flush_group(group):
            if not group:
                return

            loaders_present, branch_metadata = collect_table_loaders(group)
            metadata_present = has_metadata(branch_metadata)

            include_maven = metadata_present and published

            table_header = build_table_header(loaders_present, include_maven)

            readme_lines.append("| " + " | ".join(table_header) + " |")
            readme_lines.append("| " + " | ".join(["---"] * len(table_header)) + " |")

            for branch in group:
                raw_status = support_data.get(branch, "archived").lower()
                display_status = SUPPORT_TYPES.get(raw_status, DEFAULT_SUPPORT_TYPE)[0]

                changelog_url = f"{repo_url}/blob/{branch}/CHANGELOG.md"
                metadata_info = branch_metadata.get(branch)

                row = generate_table_row(
                    repo_url,
                    branch,
                    display_status,
                    changelog_url,
                    metadata_info,
                    loaders_present if metadata_present else [],
                    include_maven
                )

                readme_lines.append(row)

            readme_lines.append("")

        for branch in branch_list:
            _, branch_metadata = collect_table_loaders([branch])
            branch_has_metadata = has_metadata(branch_metadata)

            if current_type is None:
                current_type = branch_has_metadata

            if branch_has_metadata != current_type:
                flush_group(current_group)
                current_group = []
                current_type = branch_has_metadata

            current_group.append(branch)

        flush_group(current_group)

    readme_lines.append(f"\n---\n")
    readme_lines.extend(
        f"**{name}** — {description}\n"
        for name, description in SUPPORT_TYPES.values()
    )

    write_readme(readme_lines)


if __name__ == "__main__":
    main()
