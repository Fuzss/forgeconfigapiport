# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v5.0.0-1.19.3] - 2022-12-16
This version includes major changes and refactors regarding the whole structure of Forge Config Api Port.
### Added
- Forge Config Api Port now includes a config file itself which includes the following options:
  - An option to set the `defaultsconfigs` directory (just like Forge, not that it's too useful)
  - An option to force server configs to generate in and load from the standard config directory (`.minecraft/config/`), so those configs are no longer world specific, but can be accessed much easier by users
  - An option to manually prevent the custom `/config` command for opening local config files from being registered, intended to be used when hosting a LAN world to allow clients without this mod to connect
  - An option to apply a workaround for the `com.electronwill.nightconfig.core.io.ParsingException: Not enough data available` (an issue when reading local config files from malformed/corrupted file, which apparently appears quite often)
### Changed
- Classes not originally found in Forge now use a separate domain `fuzs.forgeconfigapiport` with a similar structure to Fabric Api (divided into `api`, `impl`, and `mixin`)
- `fuzs.forgeconfigapiport` also is the new domain used for the Maven distribution
- The `api` package at `net.minecraftforge.api` has been moved to the new domain at `fuzs.forgeconfigapiport.api` and refactored:
  - `net.minecraftforge.api.ModLoadingContext` -> `fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry`: Same as before, methods have been renamed from `registerConfig` to simply `register` though and registration needs an instance from `ForgeConfigRegistry#INSTANCE`.
  - `net.minecraftforge.api.ConfigPaths` -> `fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths`: Overhauled, includes helper methods for getting default paths for all config types, also provides the full file path, not just the directory name.
  - `net.minecraftforge.api.fml.event.config.ModConfigEvents` -> `fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents`: No changes, class has only been moved.
- All implementation related classes have been compacted and moved to `fuzs.forgeconfigapiport.impl`
- Mixin related classes have been moved to `fuzs.forgeconfigapiport.mixin`
### Removed
- This version also comes with many removals and deprecations, mainly the WIP Forge config screens have been removed, as they were barely functional and the PR on Forge's GitHub has seemingly been abandoned. As an alternative for in-game configuration, Forge Config Api Port includes default support for the [Configured (Fabric)](https://www.curseforge.com/minecraft/mc-mods/configured-fabric) mod.

## [v4.2.9-1.19.2] - 2022-12-07
### Fixed
- Fixed maven publication depending on Night Config as a mod at runtime

## [v4.2.8-1.19.2] - 2022-12-07
### Fixed
- Fixed maven publication depending on Configured mod when it's actually just optional

## [v4.2.7-1.19.2] - 2022-11-22
### Added
- Added a callback that fires when server configs are unloading 
- Added support for the [Configured] mod, it will automatically provide config screens to replace the built-in ones when installed
### Fixed
- Fixed a race condition with unloading server config

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
[Configured]: https://www.curseforge.com/minecraft/mc-mods/configured-fabric