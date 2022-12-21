# Forge Config API Port

A Minecraft mod. Downloads can be found on CurseForge.

![](https://i.imgur.com/bUAnw7w.png)

## ABOUT THE PROJECT
**!!! Forge Config API Port is in no way affiliated with the Forge project !!!**

**The sole purpose of this library is to enable usage of the Forge config api on the Fabric mod loader. This is done in the hopes of removing one more obstacle for developers wishing to maintain their mods on both loaders.**

This is a direct port from Forge, all package names are the same, so you don't even have to readjust imports when porting from Forge.
As Fabric is a whole different mod loader, there obviously have to be some differences, even though they're quite small.

For more information regarding the licensing of this project check the [LICENSING.md](LICENSING.md) file.

## DEVELOPER INFORMATION

### Adding to your workspace

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
    modImplementation "fuzs.forgeconfigapiport:forgeconfigapiport-fabric:<modVersion>"   // e.g. 5.0.0 for Minecraft 1.19.3
}
```

When developing for both Forge and Fabric simultaneously using a multi-loader setup, Forge Config Api Port can also be included in the common project to provide all classes common to both loaders. Instead of the Fabric-specific version, simply include the common publication in your `build.gradle` file.
```groovy
modImplementation "fuzs.forgeconfigapiport:forgeconfigapiport-common:<modVersion>"
```

**Versions of Forge Config Api Port for Minecraft before 1.19.3 are distributed using the `net.minecraftforge` Maven group instead of `fuzs.forgeconfigapiport`.**

It is important to note, that there is a minor difference from the production jars released on CurseForge and Modrinth: Jars from this Maven do not have a dependency set on Night Config in `fabric.mod.json`. This is necessary as there is no proper way of getting Night Config to be recognized as a Fabric mod.

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

**Also don't forget to manually add this file to your VCS, since the whole `run` directory is usually ignored by default.**

</details>

### Working with Forge Config API Port

<details>

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
As Forge's mod loading process is split into multiple stages, configs aren't loaded immediately upon being registered. On Fabric though, no such mod loading stages exist. Therefore, Forge Config API Port loads all registered configs **immediately**.

#### Listening for config loading, reloading and unloading
Forge's `net.minecraftforge.fml.event.config.ModConfigEvent.Loading` and `net.minecraftforge.fml.event.config.ModConfigEvent.Reloading` events are both adapted for Fabric's callback event style. They can be accessed from the `fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents` class. Additionally, there is an event that fires when server configs are unloading. As on Forge, all these events provide is the config being handled.

All mod config related events run on the ModLifecycle event bus instead of the primary event bus on Forge (this essentially means events do not run globally, but are instead only handed to the mod that initially registered a listener). As there is no mod specific event bus on Fabric, your mod id must be provided when registering a listener for a config callback to achieve the same behavior as on Forge, where the listener will only run for your mod.

As an example, a complete implementation of the reloading callback looks something like this:
```java
ModConfigEvents.reloading(<modId>).register((ModConfig config) -> {
    <...>
});
```

</details>

### Forge Config API Port in a multi-loader workspace

<details>

As the sole purpose of Forge Config Api Port is to allow for config parity on Forge and Fabric, it works especially great when developing your mod using a multi-loader workspace Gradle setup such as [this one](https://github.com/jaredlll08/MultiLoader-Template), arranged by [Jaredlll08](https://github.com/jaredlll08).

Configs can be created and used within the common project without having to use any abstractions at all: Simply add Forge Config API Port to the common project (use the dedicated common publication so no Fabric related code makes its way into your common project!).
```groovy
modImplementation "fuzs.forgeconfigapiport:forgeconfigapiport-common:<modVersion>"
```

As all class and package names are the same as Forge your code will compile on both Forge and Fabric without any issues. The only thing where you'll actually have to use mod loader specific code is when registering configs, that's all!

An example implementation of this can be found e.g. [here](https://github.com/thexaero/open-parties-and-claims).

</details>

### In-game configuration using Forge Config API Port

<details>

Just as with Forge itself, in-game configuration is not available in Forge Config Api Port by default. Instead, users will have to rely on third-party mods to offer that capability.

Forge Config Api Port includes default support for and recommends the [Configured (Fabric)](https://www.curseforge.com/minecraft/mc-mods/configured-fabric) mod, which already is the most popular way of handling in-game configs on Forge. To use the configs in-game [Mod Menu](https://github.com/TerraformersMC/ModMenu) needs to be installed, too.

Adding Configured and Mod Menu to your development environment is not a requirement, but highly recommended.
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
    modImplementation "curse.maven:configured-fabric-667378:4166864"    // Configured version 2.0.2 for Minecraft 1.19.3

    // Quality of Life Mods
    modRuntimeOnly "com.terraformersmc:modmenu:5.0.2"
}
```

</details>

