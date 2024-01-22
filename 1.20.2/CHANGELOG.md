# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v20.2.6-1.20.2] - 2024-01-22
### Fixed
- Fix `mods.toml` entries for NeoForge

## [v20.2.5-1.20.2] - 2024-01-13
### Changed
- Implement `UnmodifiableConfigWrapper` for `IConfigSpec` adapters on NeoForge & Forge

## [v20.2.4-1.20.2] - 2024-01-10
- Huge internal restructure, including new publications for NeoForge (for using Forge configs) and Forge (for using NeoForge configs)
- The Fabric publication still includes all classes for both config systems, the api package has been split from `fuzs.forgeconfigapiport.api.config` into `fuzs.forgeconfigapiport.fabric.api.forge` and `fuzs.forgeconfigapiport.fabric.api.neoforge`
- The common module is no longer published as `forgeconfigapiport-common`, instead there are now two common publications which are `forgeconfigapiport-common-forgeapi` and `forgeconfigapiport-common-neoforgeapi`
### Added
- Added `disableConfigWatcher` config option to both `ForgeConfigSpec` and `ModConfigSpec`
### Changed
- Updated ForgeConfigSpec with changes from [MinecraftForge#9810](https://github.com/MinecraftForge/MinecraftForge/pull/9810)
### Fixed
- Fixed `FileWatcher` hanging due to an invalid path when unloading server configs

## [v9.1.2-1.20.2] - 2023-12-02
### Changed
- Server configs can now once again be read from a local world directory, when a config file is present (it has to be manually copied there)
- New server config files are still only created in the global `.minecraft/config` directory
- Refactored `fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths`
### Fixed
- Fixed `ModConfigSpec` using a wrong package

## [v9.1.1-1.20.2] - 2023-12-01
### Fixed
- Fixed `ForgeConfigSpec` still using the old name for NeoForge

## [v9.1.0-1.20.2] - 2023-11-30
### Added
- Added support for the `net.neoforged` namespace in addition to `net.minecraftforge`
- ~~Support for Minecraft Forge will be dropped in the long term~~
- At the moment config systems from both mod loaders are fully supported, except the `/config` command which now only works for NeoForge configs

## [v9.0.0-1.20.2] - 2023-09-22
- Ported to Minecraft 1.20.2

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
