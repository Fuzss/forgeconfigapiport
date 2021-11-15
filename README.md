# Forge Config API Port

A Minecraft mod. Downloads can be found on CurseForge.

![](https://i.imgur.com/DCwxKZU.png)

## ABOUT THE PROJECT
**!!! Forge Config API Port is in no way affiliated with the Forge project !!!**

**The sole purpose of this library is to enable usage of the Forge config api on the Fabric mod loader. This is done in the hopes of removing one more obstacle for developers wishing to maintain their mods on both loaders.**

This is a direct port from Forge, all package names are the same, so you don't even have to readjust imports when porting from Forge.
As Fabric is a whole different mod loader, there obviously have to be some differences, even though they're quite small.

For more information regarding the licensing of this project check the [LICENSING.md](LICENSING.md) file.

## DEVELOPER INFORMATION

### Adding to your workspace
This project is still in development. Implementing it in your workspace is not yet recommended. Therefore, no maven repo exists at the moment.

In case you're eager to test this project, it can be included via the Curse Maven (Note: File id is found at the end of the file url). This is how it's generally done.
```groovy
repositories {
	maven { url = "https://cursemaven.com" }
}

dependencies {
    modImplementation "curse.maven:<projectName>-<projectId>:<fileId>"
}
```

### Registering configs
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

### Config loading
As Forge's mod loading process is split into multiple stages, configs aren't loaded immediately upon being registered. On Fabric though, no such mod loading stages exist. Therefore, **Forge Config API Port** loads all registered configs immediately.

### Listening for config loading and reloading
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