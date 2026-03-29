#!/usr/bin/env python3
import os
import json
import re
import subprocess
import sys
from pathlib import Path

SUPPORT_TYPES = {
    "supported": "✅ Supported",
    "limited": "⚠️ Bugfixes only",
    "archived": "❌ Archived"
}

CURSEFORGE_GAME_ID = {
    "fabric": 4,
    "forge": 1,
    "neoforge": 6
}

DEFAULT_DOWNLOADS = (
    '<img src="https://cdn.simpleicons.org/curseforge" width="14" /> '
    '[CurseForge](https://www.curseforge.com/members/fuzs_/projects) <br /> '
    '<img src="https://cdn.simpleicons.org/modrinth" width="14" /> '
    '[Modrinth](https://modrinth.com/user/Fuzs)'
)

README_FILE = Path("README.md")
VERSIONS_FILE = Path("versions.json")
BRANCH_PATTERN = re.compile(r"\d+\.\d+\.\d+")

def get_github_repo_env():
    """Optional manual fallback via command line: python generate_readme.py fallback/repo"""
    github_repo_env = os.getenv("GITHUB_REPOSITORY")
    if len(sys.argv) > 1:
        github_repo_env = sys.argv[1]

    if not github_repo_env:
        print("GITHUB_REPOSITORY not set and no fallback provided. Skipping README generation.")
        exit(0)

    return f"https://github.com/{github_repo_env}"

def load_support_data():
    if VERSIONS_FILE.exists():
        with open(VERSIONS_FILE) as f:
            return json.load(f)
    return {}

def get_all_branches():
    result = subprocess.run(
        ["git", "for-each-ref", "--format=%(refname:short)"],
        capture_output=True, text=True
    )
    return sorted([b.strip() for b in result.stdout.splitlines() if BRANCH_PATTERN.fullmatch(b.strip())], reverse=True)

def group_branches_by_mc_version(branches):
    versions = {}
    for branch in branches:
        mc_version = ".".join(branch.split(".")[:2]) + ".x"
        versions.setdefault(mc_version, []).append(branch)
    return versions

def collect_table_loaders(branch_list):
    """Return loaders present in at least one branch and metadata dict per branch"""
    loaders_present = set()
    branch_metadata = {}
    for branch in branch_list:
        metadata_path = Path(f"{branch}/metadata.json")
        if metadata_path.exists():
            with open(metadata_path) as f:
                metadata = json.load(f)
            loaders = metadata.get("platforms", [])
            branch_metadata[branch] = {"metadata": metadata, "loaders": loaders}
            loaders_present.update(loaders)
    return sorted(loaders_present), branch_metadata

def link_url(repo_url, links, name, branch, platform=None):
    for l in links:
        if l["name"].lower() == name.lower():
            slug = l.get("slug")
            if name.lower() == "curseforge":
                game_id = CURSEFORGE_GAME_ID.get(platform, 0)
                return f"https://www.curseforge.com/minecraft/mc-mods/{slug}/files/all?version={branch}&gameVersionTypeId={game_id}"
            elif name.lower() == "modrinth":
                return f"https://modrinth.com/mod/{slug}/versions?g={branch}&l={platform}"
    return repo_url

def platform_links(repo_url, links, branch, loader, branch_loaders):
    if loader in branch_loaders:
        return (
            f'<img src="https://cdn.simpleicons.org/curseforge" width="14" /> '
            f'[CurseForge]({link_url(repo_url, links, "curseforge", branch, loader)}) <br /> '
            f'<img src="https://cdn.simpleicons.org/modrinth" width="14" /> '
            f'[Modrinth]({link_url(repo_url, links, "modrinth", branch, loader)})'
        )
    return "n/a"

def generate_table_row(repo_url, branch, display_status, changelog_url, metadata_info, loader_columns):
    if metadata_info:
        metadata = metadata_info["metadata"]
        links = metadata.get("links", [])
        branch_loaders = metadata_info["loaders"]
        version = metadata["mod"]["version"]
        row = [
            f"[{branch}]({repo_url}/tree/{branch})",
            display_status,
            f"[CHANGELOG.md]({changelog_url})",
            version
        ]
        row += [platform_links(repo_url, links, branch, loader, branch_loaders) for loader in loader_columns]
    else:
        row = [
            f"[{branch}]({repo_url}/tree/{branch})",
            display_status,
            f"[CHANGELOG.md]({changelog_url})",
            DEFAULT_DOWNLOADS
        ]
    return "| " + " | ".join(row) + " |"

def write_readme(readme_lines):
    with open(README_FILE, "w") as f:
        f.write("\n".join(readme_lines))

def main():
    github_repo_env = get_github_repo_env()
    repo_url = f"https://github.com/{github_repo_env}"
    readme_lines = [f"# {github_repo_env.split('/')[-1]}"]
    support_data = load_support_data()
    branches = get_all_branches()
    mc_versions = group_branches_by_mc_version(branches)

    for mc_version, branch_list in mc_versions.items():
        readme_lines.append(f"\n### Minecraft {mc_version}\n")

        loaders_present, branch_metadata = collect_table_loaders(branch_list)

        # Build table header
        base_columns = ["Branch", "Status", "Changelog"]
        if loaders_present:
            table_header = base_columns + ["Latest"] + [loader.capitalize() for loader in loaders_present]
        else:
            table_header = base_columns + ["Downloads"]

        readme_lines.append("| " + " | ".join(table_header) + " |")
        readme_lines.append("| " + " | ".join(["-" * len(c) for c in table_header]) + " |")

        for branch in branch_list:
            raw_status = support_data.get(branch, "archived").lower()
            display_status = SUPPORT_TYPES.get(raw_status, "❌ Archived")
            changelog_url = f"{repo_url}/blob/{branch}/CHANGELOG.md"
            metadata_info = branch_metadata.get(branch)
            row = generate_table_row(repo_url, branch, display_status, changelog_url, metadata_info, loaders_present)
            readme_lines.append(row)

    write_readme(readme_lines)

if __name__ == "__main__":
    main()
