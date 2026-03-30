# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v1.1.0] - 2026-03-24

### Changed

- Replace Architectury Loom with Loom and Mod Dev Gradle

## [v1.0.17] - 2026-01-04

### Added

- Add support for [Mixin Extras](https://github.com/LlamaLad7/MixinExtras) options in the mixin config

### Changed

- Throw for mandatory dependencies when uploading a mod version
- Add more built-in external mod metadata
- Add more class migrations for Spotless configurations

## [v1.0.16] - 2025-12-15

### Changed

- Define `mandatory` field for dependencies in `NeoForgeModsToml` to support Minecraft Forge's `mods.toml` format

## [v1.0.15] - 2025-12-15

### Fixed

- Fix incorrect Fabric `environment` property for server-only mods

## [v1.0.14] - 2025-12-15

### Changed

- Move `ExternalMods` extension to the root project
- Also, dependencies are no longer loaded from JSON, but instead are purely handled in-memory
- Use Modrinth for the built-in NeoForge update checker
- Disable and deprecate `:NeoForge:refreshUpdateJson` task

### Fixed

- Fix `@NotNull` not being replaced by Spotless

## [v1.0.13] - 2025-12-14

### Added

- Add custom tasks for running `validateAccessWidener`

## [v1.0.12] - 2025-12-13

### Fixed

- Fix dependencies being added to all mod loaders when uploading

## [v1.0.11] - 2025-12-13

### Changed

- Set `isEnforceCheck` to `false` for the Spotless plugin

## [v1.0.10] - 2025-12-13

### Changed

- Adjust default Spotless rules

## [v1.0.9] - 2025-12-13

### Added

- Add [Spotless](https://github.com/diffplug/spotless)

### Changed

- Exclude the common project from the `all-build` task
- Remove some redundant compiler arguments

## [v1.0.8] - 2025-12-11

### Fixed

- Fix a wrong tag being set for GitHub publications

## [v1.0.7] - 2025-12-11

### Removed

- Revert support for multiple common subprojects

## [v1.0.6] - 2025-12-11

### Added

- Add support for multiple common subprojects

### Changed

- Dependencies now declare a list of platforms, not just one
- Disallow defining dependencies for common, will convert to existing platforms instead

## [v1.0.5] - 2025-12-11

### Added

- Add `project.isolated` Gradle property for disabling default Maven dependencies

### Fixed

- Fix root project task setup running too early

## [v1.0.4] - 2025-12-10

### Changed

- Simplify snapshot version publishing
- Prepare for some optional built-in helper mods

### Fixed

- Support data generation for versions before Minecraft 1.21.5
- Prevent adding empty remote tasks

## [v1.0.3] - 2025-12-09

### Changed

- Migrate base plugin from `fuzs.multiloader.conventions` to `fuzs.multiloader.multiloader-convention-plugins`

## [v1.0.2] - 2025-12-08

### Changed

- Verify the project platform type during configuration
- Use `fabric.loom.dontRemap` property instead of setting `targetNamespace` to `named` for `AbstractRemapJarTask`
- Remove usage of legacy Mixin annotation processor

### Fixed

- Fix `sendDiscordWebhook` not checking for the current mod version to be present in the changelog file

## [v1.0.1] - 2025-12-07

### Added

- Add some varargs helper methods in `MixinConfigJsonSpec`

### Fixed

- Fix common classes not being accessible from platform subprojects

## [v1.0.0] - 2025-12-03

### Changed

- Initial release
