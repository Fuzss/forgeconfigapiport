package net.minecraftforge.api;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class ModLoadingContext {
    /**
     * Register a new mod config, only difference from registering on Forge is <code>modId</code> has to be provided as there is no loading context to get that information from
     *
     * @param modId mod id of your mod
     * @param type type of this mod config (client, common, or server)
     * @param spec the built config spec
     *
     * @throws java.lang.IllegalArgumentException when no mod container is found for <code>modId</code>
     */
    public static void registerConfig(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        new ModConfig(type, spec, FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalArgumentException(String.format("no mod with mod id %s", modId))));
    }

    /**
     * Register a new mod config, only difference from registering on Forge is <code>modId</code> has to be provided as there is no loading context to get that information from
     *
     * @param modId mod id of your mod
     * @param type type of this mod config (client, common, or server)
     * @param spec the built config spec
     * @param fileName file name to use instead of default
     *
     * @throws java.lang.IllegalArgumentException when no mod container is found for <code>modId</code>
     */
    public static void registerConfig(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        new ModConfig(type, spec, FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalArgumentException(String.format("no mod with mod id %s", modId))), fileName);
    }
}
