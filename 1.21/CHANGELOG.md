# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.0.6-1.21] - 2024-07-24
### Added
- Add new configuration screen for NeoForge configs, must be enabled via `ConfigScreenFactoryRegistry::register`

## [v21.0.5-1.21] - 2024-07-15
### Changed
- Allow `net.minecraftforge.fml.config.ModConfig::save` to still be usable

## [v21.0.4-1.21] - 2024-07-15
### Fixed
- Fix `net.minecraftforge.fml.config.ModConfig::getFullPath` throwing a `ClassCastException`
- The class remains deprecated though and is only kept for binary compatibility

## [v21.0.3-1.21] - 2024-07-15
### Changed
- Add a few deprecation notices to highlight the internal migration to NeoForge's config system for Forge on Fabric, that will require registering Forge config classes via the NeoForge implementation in the future
- Add back contents to Forge's `ModConfig` as it is still used in config events, it now serves as an adapter to NeoForge's `ModConfig` for registered Forge configs
### Fixed
- Fix Forge config events not being called on Fabric

## [v21.0.2-1.21] - 2024-07-13
- Updated to Night Config v3.8.0
- Update for NeoForge v21.0.82
### Changed
- Remove internal Forge classes from the Fabric publication, NeoForge's system is used to benefit from the concurrency safety it provides
- There should be no breaking changes in any public facing api classes

## [v21.0.1-1.21] - 2024-07-01
- Updated for Night Config v3.7.0
### Fixed
- Common publications no longer try to pull Fabric Loader

## [v21.0.0-1.21] - 2024-06-14
- Update to Minecraft 1.21
- Forge distribution remains unpublished as long as it is not supported by Architectury Loom

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
