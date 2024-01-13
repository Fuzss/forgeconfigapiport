# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v20.4.2-1.20.4] - 2024-01-13
### Changed
- Implement `UnmodifiableConfigWrapper` for `IConfigSpec` adapters on NeoForge & Forge

## [v20.4.1-1.20.4] - 2024-01-10
- Huge internal restructure, including new publications for NeoForge (for using Forge configs) and Forge (for using NeoForge configs)
- The Fabric publication still includes all classes for both config systems, the api package has been split from `fuzs.forgeconfigapiport.api.config` into `fuzs.forgeconfigapiport.fabric.api.forge` and `fuzs.forgeconfigapiport.fabric.api.neoforge`
- The common module is no longer published as `forgeconfigapiport-common`, instead there are now two common publications which are `forgeconfigapiport-common-forgeapi` and `forgeconfigapiport-common-neoforgeapi`
### Added
- Added `disableConfigWatcher` config option to both `ForgeConfigSpec` and `ModConfigSpec`
### Changed
- Updated ForgeConfigSpec with changes from [MinecraftForge#9810](https://github.com/MinecraftForge/MinecraftForge/pull/9810)
### Fixed
- Fixed `FileWatcher` hanging due to an invalid path when unloading server configs

## [v20.4.0-1.20.4] - 2023-12-21
- Updated to Minecraft 1.20.4 (thanks to [Girafi](https://github.com/GirafiStudios) for helping out!)
- The mod version now is more streamlined by including the targeted Minecraft version (e.g. Minecraft 1.20.4 -> Mod Version 20.4.X)

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
