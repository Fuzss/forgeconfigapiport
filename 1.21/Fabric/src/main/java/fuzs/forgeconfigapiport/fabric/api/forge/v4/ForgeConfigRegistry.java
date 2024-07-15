package fuzs.forgeconfigapiport.fabric.api.forge.v4;

import fuzs.forgeconfigapiport.fabric.impl.core.ForgeConfigRegistryImpl;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Registry for adding your configs.
 * <p>
 * Note that opposed to Forge, configs are loaded and usable immediately after registration due to the lack of mod
 * loading stages on Fabric.
 */
public interface ForgeConfigRegistry {
    ForgeConfigRegistry INSTANCE = new ForgeConfigRegistryImpl();

    /**
     * Register a new mod config.
     *
     * @param modId mod id of your mod
     * @param type  type of this mod config
     * @param spec  the built config spec
     * @return the mod config instance
     *
     * @deprecated method is being removed in favor of using NeoForge's config system internally on Fabric, use
     *         {@link #register(String, net.neoforged.fml.config.ModConfig.Type, IConfigSpec)} instead.
     */
    @Deprecated(forRemoval = true)
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec);

    /**
     * Register a new mod config.
     *
     * @param modId mod id of your mod
     * @param type  type of this mod config
     * @param spec  the built config spec
     */
    void register(String modId, net.neoforged.fml.config.ModConfig.Type type, IConfigSpec<?> spec);

    /**
     * Register a new mod config.
     *
     * @param modId    mod id of your mod
     * @param type     type of this mod config
     * @param spec     the built config spec
     * @param fileName file name to use instead of default
     * @return the mod config instance
     *
     * @deprecated method is being removed in favor of using NeoForge's config system internally on Fabric, use
     *         {@link #register(String, net.neoforged.fml.config.ModConfig.Type, IConfigSpec, String)} instead.
     */
    @Deprecated(forRemoval = true)
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName);

    /**
     * Register a new mod config.
     *
     * @param modId    mod id of your mod
     * @param type     type of this mod config
     * @param spec     the built config spec
     * @param fileName file name to use instead of default
     */
    void register(String modId, net.neoforged.fml.config.ModConfig.Type type, IConfigSpec<?> spec, String fileName);
}
