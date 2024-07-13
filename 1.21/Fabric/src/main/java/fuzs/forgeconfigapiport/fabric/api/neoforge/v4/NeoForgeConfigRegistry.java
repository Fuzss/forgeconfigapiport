package fuzs.forgeconfigapiport.fabric.api.neoforge.v4;

import fuzs.forgeconfigapiport.fabric.impl.neoforge.NeoForgeConfigRegistryImpl;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

/**
 * Registry for adding your configs.
 * <p>Note that opposed to Forge, configs are loaded and usable immediately after registration due to the lack of mod loading stages on Fabric.
 */
public interface NeoForgeConfigRegistry {
    NeoForgeConfigRegistry INSTANCE = new NeoForgeConfigRegistryImpl();

    /**
     * Register a new mod config.
     * <p>An additional <code>modId</code> is required as there is no loading context to get that information from like there is on Forge.
     *
     * @param modId mod id of your mod
     * @param type  type of this mod config
     * @param spec  the built config spec
     * @return the {@link ModConfig} instance
     */
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec);

    /**
     * Register a new mod config.
     * <p>An additional <code>modId</code> is required as there is no loading context to get that information from like there is on Forge.
     *
     * @param modId    mod id of your mod
     * @param type     type of this mod config
     * @param spec     the built config spec
     * @param fileName file name to use instead of default
     * @return the {@link ModConfig} instance
     */
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec, String fileName);
}
