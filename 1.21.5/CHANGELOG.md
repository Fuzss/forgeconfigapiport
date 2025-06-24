# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.5.2-1.21.5] - 2025-06-24
### Fixed
- Temporary workaround for `com.electronwill.nightconfig.core.io.WritingException: Failed to write (REPLACE_ATOMIC)` in the Night Config library

## [v21.5.1-1.21.5] - 2025-03-27
- Re-enable support Minecraft Forge
### Changed
- Slight `/config` command changes to be more inline with NeoForge
### Fixed
- Fix broken Mod Menu integration

## [v21.5.0-1.21.5] - 2025-03-25
- Update to Minecraft 1.21.5
### Changed
- Upgrade api package to v5
- Merge `NeoForgeConfigRegistry` & `ForgeConfigRegistry` into `ConfigRegistry` on Fabric
- Remove `ForgeModConfigEvents`, use `NeoForgeModConfigEvents` instead which also works for Forge events on Fabric
- Cleanup methods in `NeoForgeConfigRegistry` and `ForgeConfigRegistry` on Forge & NeoForge respectively

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
