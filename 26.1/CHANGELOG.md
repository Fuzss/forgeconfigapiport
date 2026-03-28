# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [v26.1.0.2-mc26.1] - 2026-03-28

### Changed

- Enable support for Minecraft Forge

## [v26.1.0.1-mc26.1] - 2026-03-24

### Fixed

- Fix `accessWidener` entry missing from `fabric.mod.json` (ArchLoom used to add that automatically lol)
- Note that access transformers on NeoForge are not yet implemented, but they should not be required

## [v26.1.0.0-mc26.1] - 2026-03-24

### Changed

- Update to Minecraft 26.1
- Note for developers: This project is no longer built using Architectury Loom, having migrated to Fabric Loom and Mod
  Dev Gradle. Please report any issues particularly regarding the `Common` distribution that may arise from that
  migration.
