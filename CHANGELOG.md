# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v3.2.4-1.18.2] - 2022-08-20
### Changed
- Fabric Api is no longer bundled again, was causing issues with Quilt and was an inconvenience when bundling the mod as jar-in-jar
### Fixed
- Fixed missing sub-folders not being created for configs not in the main config directory

## [v3.2.3-1.18.2] - 2022-08-02
### Fixed
- Fixed required version of Fabric Api

## [v3.2.2-1.18.2] - 2022-08-01
### Fixed
- Fixed start-up crash due to wrong dependency

## [v3.2.1-1.18.2] - 2022-07-31
- Bundled Fabric API, it's no longer an external dependency
- Publish latest 1.18.2 build to Maven and Modrinth

## [v3.2.0-1.18.2] - 2022-03-03
- Compiled for Minecraft 1.18.2

## [v3.1.1-1.18.1] - 2022-01-10
### Fixed
- Fixed a bug where the game would crash during start-up due to the required config library not having been loaded yet

## [v3.1.0-1.18.1] - 2021-12-12
- Compiled for Minecraft 1.18.1

## [v3.0.1-1.18] - 2021-12-06
### Fixed
- Hopefully fixed a bug where config loading would very rarely lead to an exception due to the toml file format not being recognized

## [v3.0.0-1.18] - 2021-12-02
- Ported to Minecraft 1.18

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/