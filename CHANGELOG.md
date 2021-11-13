# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v1.3-1.17.1] - 2021-09-23
### Added
- Updated to Minecraft 1.17.1
- Added server-side mod component, this enables partial pick-ups (when your inventory is almost full) and pick-ups directly into some backpacks to be detected
- Added update checker
- Added mod logo for mod list
- Added support for [Catalogue] and [Configured] mods
### Changed
- Reorganized config file, it's been split into client and server sections
- Switched javascript core mod with mixins
- Accessibility backgrounds look much nicer
### Fixed
- Entry display time is no longer tied to frame rate

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
[Catalogue]: https://www.curseforge.com/minecraft/mc-mods/catalogue
[Configured]: https://www.curseforge.com/minecraft/mc-mods/configured