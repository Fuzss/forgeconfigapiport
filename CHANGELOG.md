# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.1.6-1.21.1] - 2025-11-12

### Fixed

- Fix rare `com.electronwill.nightconfig.core.io.ParsingException` when loading the internal mod config

## [v21.1.5-1.21.1] - 2025-11-09

### Fixed

- Sync `ModConfigSpec` from upstream

## [v21.1.4-1.21.1] - 2025-07-22

### Fixed

- Temporary workaround for `com.electronwill.nightconfig.core.io.WritingException: Failed to write (REPLACE_ATOMIC)` in
  the Night Config library

## [v21.1.3-1.21.1] - 2024-12-11

### Fixed

- Stop `FileWatcher` default instance on dedicated server exit

## [v21.1.2-1.21.1] - 2024-11-14

### Fixed

- Attempt fixing being unable to manually save Forge configs via `ForgeConfigSpec::save`

## [v21.1.1-1.21.1] - 2024-10-01

### Fixed

- Fix dedicated servers being unable to shut down completely

## [v21.1.0-1.21.1] - 2024-08-09

### Changed

- Update to Minecraft 1.21.1
