# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v7.0.0-1.20] - 2023-06-09
- Ported to Minecraft 1.20
### Changed-
- [Night Config](https://github.com/TheElectronWill/night-config) is now shaded instead of being included via Jar-in-Jar to allow it to be removed from `fabric.mod.json`, which was causing issues in developement environments when Night Config wasn't retrieved from the official Maven, but instead production jars were used, e.g. from the Curse Maven
- Note that Night Config is shaded without being relocated to a different package, which is necessary to stay consistent with Forge
### Removed
- Removed deprecated classes in `net.minecraftforge.api` package

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
