# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.1.0-1.19.1] - 2022-07-28
- Compiled for Minecraft 1.19.1
### Added
- Added native config screens from Forge (they are extremely buggy, so consider this an alpha, also requires Mod Menu to become accessible)
### Changed
- Ported `ForgeConfigSpec` changes from Forge
- Fabric API is no longer a dependency, relevant modules are included in the jar now

## [v4.0.2-1.19] - 2022-07-28
### Fixed
- Fixed Mod Menu being required as a Maven dependency when it's actually just optional

## [v4.0.1-1.19] - 2022-07-27
### Changed
- Maven publication no longer depends on Night Config as a Fabric mod

## [v4.0.0-1.19] - 2022-06-08
- Ported to Minecraft 1.19

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/