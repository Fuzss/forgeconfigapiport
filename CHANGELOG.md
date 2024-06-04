# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v20.6.1-1.20.6] - 2024-05-04
### Changed
- Sync upstream changes for NeoForge, most importantly adding support for startup configs (loaded immediately on NeoForge to allow retrieving values during early mod loading)
- Using this new type is not necessary on Fabric since all configs but server configs already load immediately due to the absence of mod loading stages
- The new type is simply included for parity with NeoForge
### Removed
- Remove outdated config options `recreateConfigsWhenParsingFails` and `forceGlobalServerConfigs` which are always enabled now

## [v20.6.0-1.20.6] - 2024-04-30
- Update to Minecraft 1.20.6
### Changed
- Port upstream changes for `ForgeConfigSpec` and `ModConfigSpec` 
- Overhaul server config syncing to occur during the configuration phase in favor of login
### Removed
- Remove deprecated Fabric api events and helper methods

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
