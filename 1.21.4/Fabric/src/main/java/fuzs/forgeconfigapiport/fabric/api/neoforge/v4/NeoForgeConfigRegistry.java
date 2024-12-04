package fuzs.forgeconfigapiport.fabric.api.neoforge.v4;

import fuzs.forgeconfigapiport.fabric.impl.core.NeoForgeConfigRegistryImpl;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

/**
 * Registry for adding your configs.
 * <p>
 * Note that opposed to NeoForge, configs are loaded and usable immediately after registration due to the lack of mod
 * loading stages on Fabric.
 * <p>
 * TODO rename to ConfigRegistry and move methods for registering Forge's ConfigSpec in here, also remove ModConfig return values
 */
public interface NeoForgeConfigRegistry {
    NeoForgeConfigRegistry INSTANCE = new NeoForgeConfigRegistryImpl();

    /**
     * Register a new mod config.
     * <p>
     * Note that {@link ModConfig} will be turned into an internal class, which will remove the return value from this
     * method in the future.
     *
     * @param modId mod id of your mod
     * @param type  type of this mod config
     * @param spec  the built config spec
     * @return the mod config instance
     */
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec);

    /**
     * Register a new mod config.
     * <p>
     * Note that {@link ModConfig} will be turned into an internal class, which will remove the return value from this
     * method in the future.
     *
     * @param modId    mod id of your mod
     * @param type     type of this mod config
     * @param spec     the built config spec
     * @param fileName file name to use instead of default
     * @return the mod config instance
     */
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec, String fileName);
}
