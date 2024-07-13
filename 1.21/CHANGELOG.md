# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
