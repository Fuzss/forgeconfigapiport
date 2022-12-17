package fuzs.forgeconfigapiport.api.config.v2;

import fuzs.forgeconfigapiport.impl.config.ForgeConfigRegistryImpl;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Registry for adding your configs. On Forge this is done using <code>net.minecraftforge.fml.ModLoadingContext</code>, which does not exist in Forge Config Api Port.
 * <p>Note that opposed to Forge, configs are loaded and usable immediately after registration.
 */
public interface ForgeConfigRegistry {
    /**
     * implementation instance for registering configs
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
