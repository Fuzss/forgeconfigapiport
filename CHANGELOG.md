# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

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
