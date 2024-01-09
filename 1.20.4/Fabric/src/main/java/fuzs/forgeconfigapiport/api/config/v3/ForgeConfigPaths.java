package fuzs.forgeconfigapiport.api.config.v3;

import fuzs.forgeconfigapiport.fabric.impl.config.legacy.ForgeConfigPathsV3Impl;

import java.nio.file.Path;

/**
 * Access to paths where different kinds of config files are stored by Forge.
 */
public interface ForgeConfigPaths {
    /**
     * Implementation instance for retrieving config paths.
     */
    ForgeConfigPaths INSTANCE = new ForgeConfigPathsV3Impl();

    /**
     * The directory where configs are stored at, usually this points to <code>.minecraft/config</code>.
     *
     * @return config path
     */
    Path getConfigDirectory();

    /**
     * Path where default configs are stored, by default inside of <code>.minecraft/defaultconfigs</code>.
     * <p>This path is configurable via the Forge Config Api Port config.
     * <p>Default configs are mainly intended for server configs to allow pre-configured configs to be applied to every
     * newly created world (since server configs are handled per world). This mechanic also applies to other config types (although they are only created once).
     *
     * @return default configs path
     */
    Path getDefaultConfigsDirectory();
}
