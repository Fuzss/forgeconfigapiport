# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v2.0.5-1.17.1] - 2022-08-20
### Changed
- Fabric Api is no longer bundled again, was causing issues with Quilt and was an inconvenience when bundling the mod as jar-in-jar
### Fixed
- Fixed missing sub-folders not being created for configs not in the main config directory

## [v2.0.4-1.17.1] - 2022-08-02
### Fixed
- Fixed required version of Fabric Api

## [v2.0.3-1.17.1] - 2022-08-02
- Bundled Fabric API, it's no longer an external dependency
- Publish latest 1.17.1 build to Maven and Modrinth

## [v2.0.2-1.17.1] - 2022-01-10
### Fixed
- Fixed a bug where the game would crash during start-up due to the required config library not having been loaded yet

## [v2.0.1-1.17.1] - 2021-12-06
### Fixed
- Hopefully fixed a bug where config loading would very rarely lead to an exception due to the toml file format not being recognized

## [v2.0.0-1.17.1] - 2021-11-15
- Initial release

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
