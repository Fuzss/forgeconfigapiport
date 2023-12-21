# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v20.4.0-1.20.4] - 2023-12-21
- Updated to Minecraft 1.20.4 (thanks to [Girafi](https://github.com/GirafiStudios) for helping out!)
- The mod version now is more streamlined by including the targeted Minecraft version (e.g. Minecraft 1.20.4 -> Mod Version 20.4.X)

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
- Support for Minecraft Forge will be dropped in the long term
- At the moment config systems from both mod loaders are fully supported, except the `/config` command which now only works for NeoForge configs

## [v9.0.0-1.20.2] - 2023-09-22
- Ported to Minecraft 1.20.2

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
