# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.2.6-1.19.2] - 2022-08-31
### Fixed
- Fixed crash when a config screen attempts to handle unsupported config value types

## [v4.2.5-1.19.2] - 2022-08-31
### Added
- Added a new `ModConfigEvents` class which is the new way of handling mod config events (loading and reloading), to ensure config events are only accessed on a mod specific basis
- The old `ModConfigEvent` class is now deprecated for removal in the next major release for 1.20
### Changed
- Reverted minor internal removals from previous version to ensure compatibility with mods using those internals; they are deprecated now instead
- Added a bunch of `@ApiStatus` annotations to ensure mods are only accessing the parts of the library they are meant to
- This will be overhauled in the next major release for 1.20 to move some internals to a dedicated `impl` domain

## [v4.2.4-1.19.2] - 2022-08-30
- Resolved issues regarding the license of Forge
### Changed
- Ported more changes from Forge's config screens
- Switched remaining loggers from Log4j to SLF4J

## [v4.2.3-1.19.2] - 2022-08-22
### Fixed
- Fixed config screen crashing for config values without a widget factory

## [v4.2.2-1.19.2] - 2022-08-21
### Fixed
- Added missing translation keys when cancelling config editing

## [v4.2.1-1.19.2] - 2022-08-21
### Changed
- Ported most recent changes from Forge's config screens
### Fixed
- Fixed minimum Minecraft version requirement
- Fixed multiple entries being selected in gui lists

## [v4.2.0-1.19.2] - 2022-08-20
- Compiled for Minecraft 1.19.2
### Changed
- Fabric Api is no longer bundled again, was causing issues with Quilt and was an inconvenience when bundling the mod as jar-in-jar
### Fixed
- Fixed missing sub-folders not being created for configs not in the main config directory

## [v4.1.4-1.19.1] - 2022-08-02
### Fixed
- Fixed required version of Fabric Api

## [v4.1.3-1.19.1] - 2022-07-31
- Include Fabric Lifecycle Events which is also required

## [v4.1.2-1.19.1] - 2022-07-28
- Now requires Minecraft 1.19.1 or newer
### Fixed
- Fix start-up crash due to wrong mixin file location in publishing jar

## [v4.1.1-1.19.1] - 2022-07-28
- Re-compile to update outdated files

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