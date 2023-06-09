# Forge Config API Port

A Minecraft mod. Downloads can be found on [CurseForge](https://www.curseforge.com/members/fuzs_/projects) and [Modrinth](https://modrinth.com/user/Fuzs).

![](https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/forgeconfigapiport/banner.png)

## ABOUT THE PROJECT
**!!! Forge Config API Port is in no way affiliated with the Minecraft Forge project !!!**

**The sole purpose of this library is to enable usage of the Minecraft Forge config api on both Fabric and Quilt mod loaders. This is done in the hopes of removing one more obstacle for developers wishing to maintain their mods on various mod loaders.**

This is a direct port from Minecraft Forge, all package names are the same, so you don't even have to readjust imports when porting from Forge.
As Fabric and Quilt are very different mod loaders, there obviously have to be some differences, even though they're quite small.

The main advantage of Forge Config Api Port over other config libraries lies in the fact that no additional library is required on Forge (since this very exact config api is built-in), only Fabric/Quilt needs this one library.

For more information regarding the licensing of this project and different parts of it check the [LICENSING.md](LICENSING.md) file.

## DEVELOPER INFORMATION

### Adding Forge Config Api Port to your Gradle workspace

<details>

#### Via Fuzs Mod Resources
Fuzs Mod Resources is the recommended way of adding Forge Config API Port to your project in your `build.gradle` file.
```groovy
repositories {
    maven {
        name = "Fuzs Mod Resources"
        url = "https://raw.githubusercontent.com/Fuzss/modresources/main/maven/"
    }
}

dependencies {
    modApi "fuzs.forgeconfigapiport:forgeconfigapiport-fabric:<modVersion>"   // e.g. 5.0.0 for Minecraft 1.19.3
}
```

When developing for both multiple mod loaders simultaneously using a multi-loader setup, Forge Config Api Port can also be included in the common project to provide all classes common to both loaders. Instead of the mod loader specific version, simply include the common publication in your `build.gradle` file.
```groovy
api "fuzs.forgeconfigapiport:forgeconfigapiport-common:<modVersion>"
```

**Versions of Forge Config Api Port for Minecraft before 1.19.3 are distributed using the `net.minecraftforge` Maven group instead of `fuzs.forgeconfigapiport`.**

It is important to note, that there is a minor difference from the production jars released on CurseForge and Modrinth: Jars from this Maven do not have a dependency set on Night Config in `fabric.mod.json`. This is necessary as there is no proper way of getting Night Config to be recognized as a Fabric/Quilt mod.

With this in mind, when you plan to `include` Forge Config API Port as a Jar-in-Jar, absolutely make sure to set a proper dependency on Night Config within your own mod's `fabric.mod.json`, since Forge Config API Port won't have any set.
```json
{
  "depends": {
    "com_electronwill_night-config_core": "*",
    "com_electronwill_night-config_toml": "*"
  }
}
```

#### Via Curse Maven
Alternatively you can use the Curse Maven to include this library in your workspace. (Note: project name is merely a descriptor, you should be able to choose it freely; project id is found in the info box of a project page, file id is found at the end of the file url) This is how adding a Curse Maven dependency is generally done:

Since the Curse Maven generally isn't aware of any maven dependencies, you have to add those manually. They are only required within your workspace, in a production environment those dependencies are shipped with Forge Config API Port.
```groovy
repositories {
    mavenCentral()
    maven {
        name = 'Curse Maven'
        url = 'https://cursemaven.com'
    }
}

dependencies {
        implementation 'com.electronwill.night-config:core:3.6.5'
        implementation 'com.electronwill.night-config:toml:3.6.5'
    	modImplementation "curse.maven:<projectName>-<projectId>:<fileId>"  // e.g. forgeconfigapiport-547434:3671141 for mod version 3.2.0 for Minecraft 1.18.2, all required ids for this version are found here: https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric/files/3671141
}
```

There's also one more thing that will have to be done: When including Forge Config API Port from the Curse Maven, the mod will not be able to recognize the required Night Config libraries. You'll know that is the case when upon running the game, you are greeted with the following message:
```
 net.fabricmc.loader.impl.FormattedException: net.fabricmc.loader.impl.discovery.ModResolutionException: Mod resolution encountered an incompatible mod set!
A potential solution has been determined:
	 - Install com_electronwill_night-config_core, any version.
	 - Install com_electronwill_night-config_toml, any version.
```
To resolve this issue, manually add dependency overrides (check the [Fabric Wiki](https://fabricmc.net/wiki/tutorial:dependency_overrides) for more information on this topic) to your run configuration. Do that by creating a new file at `run/config/fabric_loader_dependencies.json`, in which you put the following contents:
```json
{
  "version": 1,
  "overrides": {
    "forgeconfigapiport": {
      "-depends": {
        "com_electronwill_night-config_core": "",
        "com_electronwill_night-config_toml": ""
      }
    }
  }
}
```

**Also don't forget to manually add this file to your VCS, since the whole `run` directory is usually ignored by default. A dedicated common publication does not exist for these versions.**

</details>

### Working with Forge Config API Port

<details>

**These instructions exist only to highlight differences between the original Forge implementation and Forge Config Api Port. It is assumed you are generally familiar with Forge's configs.**

#### Registering configs
The recommended point for registering your configs is directly in your `ModInitializer::onInitialize` method.

Registering your configs works via `fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry`, obtain an instance of the implementation from `ForgeConfigRegistry#INSTANCE`.

You'll have to provide the mod id of your mod, as there is no context which would be aware of the current mod as there is on Forge.
```java
void register(String modId, ModConfig.Type type, IConfigSpec<?> spec)
```
And as on Forge there is also a version which supports a custom file name.
```java
void register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName)
```

#### Config loading
As Forge's mod loading process is split into multiple stages, configs aren't loaded immediately upon being registered. On Fabric/Quilt though, no such mod loading stages exist. Therefore, Forge Config API Port loads all registered configs **immediately**.

#### Listening for config loading, reloading and unloading
Forge's `net.minecraftforge.fml.event.config.ModConfigEvent.Loading` and `net.minecraftforge.fml.event.config.ModConfigEvent.Reloading` events are both adapted for Fabric's/Quilt's callback event style. They can be accessed from the `fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents` class. Additionally, there is an event that fires when server configs are unloading. As on Forge, all these events provide is the config being handled.

All mod config related events run on the ModLifecycle event bus instead of the primary event bus on Forge (this essentially means events do not run globally, but are instead only handed to the mod that initially registered a listener). As there is no mod specific event bus on Fabric/Quilt, your mod id must be provided when registering a listener for a config callback to achieve the same behavior as on Forge, where the listener will only run for your mod.

As an example, a complete implementation of the reloading callback looks something like this:
```java
ModConfigEvents.reloading(<modId>).register((ModConfig config) -> {
    <...>
});
```

</details>

### Other differences from Forge in Forge Config Api Port

<details>

Apart from the obviously necessary differences in implementation details from Forge mentioned above, Forge Config Api Port additionally includes minor tweaks to certain aspects of the config system. These tweaks are optional via a separate config file (found at `.minecraft/config/forgeconfigapiport.toml`) and only concern the implementation of certain features, their implementations do **NOT** result in changes to public facing code.

These options are also available for Forge as a separate mod project: [Night Config Fixes](https://www.curseforge.com/minecraft/mc-mods/night-config-fixes)

#### A fix for `ParsingException: Not enough data available`

> recreateConfigsWhenParsingFails = true

If your game has ever crashed with the following exception, this workaround is just for you and the main reason why Night Config Fixes was made in the first place:
> Caused by: com.electronwill.nightconfig.core.io.ParsingException: Not enough data available

Sometimes and very randomly (also only reported on Windows systems), existing config files just loose all of their data and go completely blank. This is when the exception mentioned above is thrown, as Night Config is unable to read the file.

With this workaround enabled, instead of the game crashing, the invalid blank file is simply deleted and a new file with default values is created in its place. No settings from the previous file can be restored, unfortunately.

**Note:**  
When enabling this workaround in a mod pack which ships some already configured configs, make sure to place those configs in the `defaultconfigs` directory, not just in `config`, so that when restoring a faulty config the desired default values from `defaultconfigs` are used instead of the built-in values.

#### Apply default config values from `defaultconfigs`

> correctConfigValuesFromDefaultConfig = true

When only individual options in a config are invalid, like an option is missing or contains a set value that cannot be parsed, Forge corrects those individual options by restoring them to their default values in the config file. You can observe Forge doing this in the console when the following message is printed:

> [net.minecraftforge.fml.config.ConfigFileTypeHandler/CONFIG]: Configuration file CONFIG_PATH is not correct. Correcting

The problem with that is, Forge uses the built-in default value defined by the mod providing the config, but ignores any value from a possibly present default config in `defaultconfigs` which a mod pack might ship.

This workaround changes this behavior and checks if an entry in a config in `defaultconfigs` exists first before falling back to correcting to the built-in default value.

**Example:**  
A config contains an option which requires an integer value.  
The default value for this option defined by the mod the config is from is 3.  
The default value for this option defined by the current mod pack via the config placed in `defaultconfigs` is 5 though.  
When the user now accidentially enters a value such as 10.5, Forge corrects the input back to the default 3 (since 10.5 is a double, not an integer and therefore invalid).  
With this workaround enabled the value will instead be corrected to 5.

#### Global server configs

> forceGlobalServerConfigs = true

Changes Forge's server config type to generate in the global `config` directory, instead of on a local basis per world in `saves/WORLD_NAME/serverconfig`.

This design decision by Forge simply causes too much confusion and frustration among users, so this mod felt like a good enough opportunity to include a fix for that.

</details>

### Forge Config API Port in a multi-loader workspace

<details>

As the sole purpose of Forge Config Api Port is to allow for config parity on Forge and Fabric/Quilt, it works especially great when developing your mod using a multi-loader workspace Gradle setup such as [this one](https://github.com/jaredlll08/MultiLoader-Template), arranged by [Jaredlll08](https://github.com/jaredlll08).

Configs can be created and used within the common project without having to use any abstractions at all: Simply add Forge Config API Port to the common project (use the dedicated common publication so no mod loader specific code makes its way into your common project!).
```groovy
api "fuzs.forgeconfigapiport:forgeconfigapiport-common:<modVersion>"
```

As all class and package names are the same as Forge your code will compile on both Forge and Fabric/Quilt without any issues. The only thing where you'll actually have to use mod loader specific code is when registering configs, that's all!

An example implementation of this can be found [here](https://github.com/thexaero/open-parties-and-claims).

</details>

### In-game configuration using Forge Config API Port

<details>

Just as with Forge itself, in-game configuration is not available in Forge Config Api Port by default. Instead, users will have to rely on third-party mods to offer that capability.

Forge Config Api Port includes default support for and recommends the [Configured (Fabric)](https://www.curseforge.com/minecraft/mc-mods/configured-fabric) mod, which already is the most popular way of handling in-game configs on Forge. To use the configs provided by Configured in-game [Mod Menu](https://github.com/TerraformersMC/ModMenu) needs to be installed, too.

Adding Configured and Mod Menu to your workspace is not a requirement, but highly recommended.
```groovy
repositories {
    maven {
        name = 'Curse Maven'
        url = 'https://cursemaven.com'
    }
    maven {
        name = 'Terraformers'
        url = "https://maven.terraformersmc.com/"
    }
}

dependencies {
    // Configured
    modLocalRuntime "curse.maven:configured-fabric-667378:4166864"    // Configured version 2.0.2 for Minecraft 1.19.3

    // Mod Menu
    modLocalRuntime "com.terraformersmc:modmenu:5.0.2"
}
```

</details>

### Config library alternatives to Forge's configs

<details>

Forge Config Api Port is really useful when porting a Forge mod to Fabric/Quilt (or maintaining a mod on multiple loaders) as the existing config implementation does not need any major adjustments, and also no new library dependency needs to be added to the Forge project.

As with every library though, the Forge config system does have a number of shortcomings, such as:
- Existence of three different config types with different functionalities that can be annoying to work with as a developer and easily become confusing for users
- Handling of server config files per world, without easy access to the file, and a major effort when changing server config values globally (this is addressed by Forge Config Api Port by moving server configs to the common config directory in `.minecraft/config/`)
- Lack of in-game configuration (possible via the third-party [Configured] mod though)
- Lack of annotation support when defining new config files

So when starting on a brand-new mod project, it might be advisable to consider a completely different config library with more features than the Forge system has. Here is an overview with some recommendations:

| Project               | Forge | Fabric | Quilt | Minecraft Versions                 | Comments                                                                                                                                                        |
|-----------------------|-------|--------|-------|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Forge Config Api      | ✅     | ✅      | ✅     | 1.16, 1.17, 1.18, 1.19             | Fabric and Quilt support provided by Forge Config Api Port. In-game config screens provided by the [Configured] mod.                                            |
| [Spectre Lib]         | ✅     | ✅      | ✅     | 1.19                               | Not primarily a config library, implementation is very much based on Forge's configs. No in-game config screens.                                                |
| [Pollen]              | ✅     | ✅      | ✅     | 1.16, 1.18                         | Not primarily a config library, implementation is very much based on Forge's configs. No in-game config screens.                                                |
| [Cloth Config Api]    | ✅     | ✅      | ✅     | 1.14, 1.15, 1.16, 1.17, 1.18, 1.19 | Very extensive config library with annotation support, in-game config screens, and great api documentation.                                                     |
| [Omega Config]        | ❌     | ✅      | ✅     | 1.18, 1.19                         | Config library with annotation support and in-game config screens.                                                                                              |
| [Owo Lib]             | ❌     | ✅      | ✅     | 1.17, 1.18, 1.19                   | Not primarily a config library, with annotation support and the most beautiful in-game config screens (customizable via XML!). Also the best api documentation. |
| [Midnight Lib]        | ✅     | ✅      | ✅     | 1.17, 1.18, 1.19                   | Great config library with in-game screens, also contains non-config related features though.                                                                    |
| [YetAnotherConfigLib] | ❌     | ✅      | ✅     | 1.19                               | Great library with lots of features, in-game screens are heavily inspired by Sodium. Great api documentation.                                                   |

</details>

[Configured]: https://www.curseforge.com/minecraft/mc-mods/configured
[Spectre Lib]: https://github.com/illusivesoulworks/spectrelib
[Pollen]: https://www.curseforge.com/minecraft/mc-mods/pollen
[Cloth Config Api]: https://www.curseforge.com/minecraft/mc-mods/cloth-config
[Omega Config]: https://www.curseforge.com/minecraft/mc-mods/omega-config
[Owo Lib]: https://www.curseforge.com/minecraft/mc-mods/owo-lib
[Midnight Lib]: https://modrinth.com/mod/midnightlib
[YetAnotherConfigLib]: https://github.com/isXander/YetAnotherConfigLib
