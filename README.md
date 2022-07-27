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
#### Via Fuzs Mod Resources
Fuzs Mod Resources is the recommended way of adding Forge Config API Port to your project.
```groovy
repositories {
    maven {
        name = "Fuzs Mod Resources"
        url = "https://raw.githubusercontent.com/Fuzss/modresources/main/maven/"
    }
}

dependencies {
        modImplementation "net.minecraftforge:forgeconfigapiport-fabric:<modVersion>"   // e.g. 4.0.0 for Minecraft 1.19
}
```

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

Since the Curse Maven generally isn't aware of any maven dependencies, you might have to add those manually, too. They are only required within your workspace, in a production environment those dependencies are shipped with Forge Config API Port.
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

There's also one more thing that might have to be done: Depending on how you have enabled Forge Config API Port in your environment, the mod might not be able to recognize the required Night Config libraries. You'll know that is the case when upon running the game instance, you'll be greeted by this message:
```
 net.fabricmc.loader.impl.FormattedException: net.fabricmc.loader.impl.discovery.ModResolutionException: Mod resolution encountered an incompatible mod set!
A potential solution has been determined:
	 - Install com_electronwill_night-config_core, any version.
	 - Install com_electronwill_night-config_toml, any version.
```
To resolve this issue, what you need to do is add dependency overrides (check the [Fabric Wiki](https://fabricmc.net/wiki/tutorial:dependency_overrides) for more information on this topic) for your configuration. Do that by creating a new file at `run/config/fabric_loader_dependencies.json`, in which you put the following contents:
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
Also don't forget to manually add this file to your VCS, since the whole `run` directory is usually ignored by default.

### Working with Forge Config API Port
#### Registering configs
The recommended point for registering your configs is directly in your `ModInitializer::onInitialize` method.

Registering your configs still works via a class called `net.minecraftforge.api.ModLoadingContext`, though the name is only for mimicking Forge, as this is really only used for registering configs.

You'll have to provide the mod id of your mod, as there is no context which would be aware of the current mod.
```java
public static void registerConfig(String modId, ModConfig.Type type, IConfigSpec<?> spec)
```
And as on Forge there is also a version which supports a custom file name.
```java
public static void registerConfig(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName)
```

#### Config loading
As Forge's mod loading process is split into multiple stages, configs aren't loaded immediately upon being registered. On Fabric though, no such mod loading stages exist. Therefore, Forge Config API Port loads all registered configs immediately.

#### Listening for config loading and reloading
Forge's `ModConfigEvent.Loading` and `ModConfigEvent.Reloading` events are both adapted for Fabric's callback event style. They can be accessed from the `net.minecraftforge.api.fml.event.config.ModConfigEvent` class.

As on Forge, all these events provide is the config that is loading / reloading. But unlike on Forge, when processing that config, you'll have to make sure it actually comes from your mod. This is important, as there is no mod specific event bus on Fabric, meaning all events are fired for all mods subscribed to them.

As an example, a complete implementation of the reloading callback looks something like this:
```java
ModConfigEvent.RELOADING.register((ModConfig config) -> {
    if (config.getModId().equals(<modId>)) {
        ...
    }
});
```

### Forge Config API Port in a multi-loader workspace
As the sole purpose of this library is to allow for config parity on Forge and Fabric, Forge Config API Port works especially great when developing your mod using a multi-loader workspace Gradle setup such as [this one](https://github.com/jaredlll08/MultiLoader-Template), arranged by [Jaredlll08](https://github.com/jaredlll08).

Configs can extremely easily be dealt with in the common project without having to use any abstractions at all: Simply add Forge Config API Port to the common project, since all class and package names are the same as on Forge your code will compile on both Forge and Fabric without any issues.

The only thing where you'll actually have to use mod loader specific code is when registering configs, that's all! Just make sure to stay away from the `net.minecraftforge.api` package in the common project, as that is where Fabric specific code is located (this will no longer be accessible from common in the future, by splitting the Maven publication into `common` and `fabric` sub-projects).

A complete implementation of this can be found e.g. [here](https://github.com/thexaero/open-parties-and-claims).

### In-game configuration using Forge Config API Port
In-game configursation has not yet been implemented, as it is not available on Forge itself. A PR adding such a simple system to Forge is currently pending, once approved it will be ported to Forge Config API Port as soon as possible. Config screens will then be accessible using [Mod Menu](https://github.com/TerraformersMC/ModMenu)
