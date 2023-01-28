# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v5.0.7-1.19.3] - 2023-01-28
### Changed
- The `/config` command now is registered as a client-side command to prevent issues with local multiplayer when vanilla clients are trying to connect (thanks for the suggestion [DJtheRedstoner](https://github.com/DJtheRedstoner)!)
- As a result Forge Config API Port's `disableConfigCommand` config option has become obsolete and was therefore removed
- Some further improvements to the `/config` command itself
  - Invalid suggestions for mod id are no longer shown
  - Supports multiple configs per config type for a single mod (if present)
  - Disabled access to server configs when playing on a dedicated server which would throw an exception before 
- Deprecated Forge's config command implementation, it is no longer used and will be removed in the future as it's not a relevant part of the api

## [v5.0.6-1.19.3] - 2023-01-09
### Fixed
- Fixed faulty Fabric maven publication from previous release

## [v5.0.5-1.19.3] - 2023-01-09
### Fixed
- Update `fuzs.forgeconfigapiport.impl.util.ReflectionHelper` to fix crash with the Configured mod when editing a value that does not have a valid `net.minecraftforge.common.ForgeConfigSpec$Range`

## [v5.0.4-1.19.3] - 2023-01-07
### Changed
- Server configs are now loaded before everything else in Fabric's `ServerLifecycleEvents#SERVER_STARTING` callback, and are unloaded after everything in `ServerLifecycleEvents#SERVER_STOPPED` via custom event phases (thanks to [SquidDev](https://github.com/SquidDev)!)
- Sources are once again published to CurseForge and Modrinth
### Fixed
- Removed test config accidentally included with the last release

## [v5.0.3-1.19.3] - 2022-12-23
### Fixed
- Fixed invalid mixin file reference in jar published to Maven

## [v5.0.2-1.19.3] - 2022-12-23
### Changed
- Some internal restructuring to move more code to the common project, to allow for easier Quilt support when it's ready

## [v5.0.1-1.19.3] - 2022-12-22
### Fixed
- Fixed Maven jar containing an unprocessed `fabric.mod.json`
- Fixed optional mods missing from CurseForge and Modrinth publications

## [v5.0.0-1.19.3] - 2022-12-21
This version includes major changes and refactors regarding the whole structure of Forge Config Api Port, most notably a reorganization into a multi-loader project (with separate publications for a common and Fabric Gradle project); as well as moving all classes and code not present on Forge to a separate domain (`fuzs.forgeconfigapiport`).
### Added
- Forge Config Api Port now includes a config file itself which includes the following options:
  - An option to set the `defaultsconfigs` directory (just like Forge, not that it's too useful)
  - An option to force server configs to generate in and load from the standard config directory (`.minecraft/config/`), so those configs are no longer world specific, but can be accessed much easier by users
  - An option to manually prevent the custom `/config` command for opening local config files from being registered, intended to be used when hosting a LAN world to allow clients without this mod to connect
  - An option to apply a workaround for the `com.electronwill.nightconfig.core.io.ParsingException: Not enough data available` (an issue when reading local config files from malformed/corrupted file, which apparently appears quite often)
### Changed
- Forge Config Api Port now comes with two publications: `forgeconfigapiport-common` and `forgeconfigapiport-fabric`. When developing a mod for Fabric only, simply keep using `forgeconfigapiport-fabric`, nothing different from before. But when developing a mod for both Forge and Fabric simultaneously using a multi-loader setup, `forgeconfigapiport-common` comes in handy for the common project, as it enables using most config related classes in that part of the project, really just config registration is what's left for the mod loader specific projects.
- Classes not originally found in Forge now use a separate domain `fuzs.forgeconfigapiport` with a similar structure to Fabric Api (divided into `api`, `impl`, and `mixin`)
- `fuzs.forgeconfigapiport` also is the new domain used for the Maven distribution
- The `api` package at `net.minecraftforge.api` has been moved to the new domain at `fuzs.forgeconfigapiport.api` and refactored:
  - `net.minecraftforge.api.ModLoadingContext` -> `fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry`: Same as before, methods have been renamed from `registerConfig` to simply `register` though and registration needs an instance from `ForgeConfigRegistry#INSTANCE`.
  - `net.minecraftforge.api.ConfigPaths` -> `fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths`: Overhauled, includes helper methods for getting default paths for all config types, also provides the full file path, not just the directory name.
  - `net.minecraftforge.api.fml.event.config.ModConfigEvents` -> `fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents`: No changes, class has only been moved.
- All implementation related classes have been compacted and moved to `fuzs.forgeconfigapiport.impl`
- Mixin related classes have been moved to `fuzs.forgeconfigapiport.mixin`
### Removed
- This version also comes with many removals and deprecations, mainly the WIP Forge config screens have been removed, as they were barely functional and the PR on Forge's GitHub has seemingly been abandoned. As an alternative for in-game configuration, Forge Config Api Port includes default support for and recommends the [Configured (Fabric)](https://www.curseforge.com/minecraft/mc-mods/configured-fabric) mod.

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
[Configured]: https://www.curseforge.com/minecraft/mc-mods/configured-fabric