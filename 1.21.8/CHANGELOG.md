# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.8.2-1.21.8] - 2025-11-12

### Fixed

- Fix rare `com.electronwill.nightconfig.core.io.ParsingException` when loading the internal mod config

## [v21.8.1-1.21.8] - 2025-08-29

### Changed

- Update Night Config to v3.8.3
- Unload server configs after disconnecting from server ([#2459](https://github.com/neoforged/NeoForge/pull/2459))
- Make ModConfig.getFullPath nullable ([#326](https://github.com/neoforged/FancyModLoader/pull/326))
- Unload non-file configs ([#329](https://github.com/neoforged/FancyModLoader/pull/329))

## [v21.8.0-1.21.8] - 2025-07-18

### Changed

- Update to Minecraft 1.21.8
