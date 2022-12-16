package net.minecraftforge.api;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * class to mimic Forge's ModLoadingContext, name is misleading in this case though, as this is only used for registering configs
 *
 * <p>package is purposefully different from Forge, as the class itself works completely differently and is not compatible with the implementation on Fore
 *
 * @deprecated Replaced by {@link ForgeConfigRegistry}.
 */
@Deprecated(forRemoval = true)
public final class ModLoadingContext {

    /**
     * Register a new mod config, only difference from registering on Forge is <code>modId</code> has to be provided as there is no loading context to get that information from
     *
     * @param modId mod id of your mod
     * @param type type of this mod config (client, common, or server)
     * @param spec the built config spec
     *
     * @throws IllegalArgumentException when no mod container is found for <code>modId</code>
     * @return the {@link ModConfig} instance
     */
    public static ModConfig registerConfig(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        return ForgeConfigRegistry.INSTANCE.register(modId, type, spec);
    }

    /**
     * Register a new mod config, only difference from registering on Forge is <code>modId</code> has to be provided as there is no loading context to get that information from
     *
     * @param modId mod id of your mod
     * @param type type of this mod config (client, common, or server)
     * @param spec the built config spec
     * @param fileName file name to use instead of default
     *
     * @throws IllegalArgumentException when no mod container is found for <code>modId</code>
     * @return the {@link ModConfig} instance
     */
    public static ModConfig registerConfig(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        return ForgeConfigRegistry.INSTANCE.register(modId, type, spec, fileName);
    }
}
