package fuzs.forgeconfigapiport.api.config.v3;

import fuzs.forgeconfigapiport.impl.config.ForgeConfigRegistryImpl;
import net.minecraftforge.fml.config.ModConfig;
import net.neoforged.fml.config.IConfigSpec;

/**
 * Registry for adding your configs. Opposed to Fabric/Quilt this registry serves as a bridge between NeoForge's and Forge's config systems.
 * NeoForge configs supplied by Forge Config Api Port are automatically adapted as Forge configs. Therefore, the returned {@link ModConfig} instance is the original Forge mod config.
 * <p>As configs are added to Forge's native config system, original restrictions apply, and none of the changes implemented by Forge Config Api Port on Fabric/Quilt hold,
 * like loading client and common configs immediately upon registration as well as loading server configs from the global <code>.minecraft/config</code> directory.
 */
public interface ForgeConfigRegistry {
    /**
     * Implementation instance for registering configs.
     */
    ForgeConfigRegistry INSTANCE = new ForgeConfigRegistryImpl();

    /**
     * Register a new mod config, only difference from registering on Forge is <code>modId</code> has to be provided as there is no loading context to get that information from
     *
     * @param modId mod id of your mod
     * @param type  type of this mod config (client, common, or server)
     * @param spec  the built config spec
     * @return the {@link ModConfig} instance
     *
     * @throws IllegalArgumentException when no mod container is found for <code>modId</code>
     */
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec);

    /**
     * Register a new mod config, only difference from registering on Forge is <code>modId</code> has to be provided as there is no loading context to get that information from
     *
     * @param modId    mod id of your mod
     * @param type     type of this mod config (client, common, or server)
     * @param spec     the built config spec
     * @param fileName file name to use instead of default
     * @return the {@link ModConfig} instance
     *
     * @throws IllegalArgumentException when no mod container is found for <code>modId</code>
     */
    ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName);
}
