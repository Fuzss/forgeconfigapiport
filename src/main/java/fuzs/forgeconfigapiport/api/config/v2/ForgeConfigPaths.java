package fuzs.forgeconfigapiport.api.config.v2;

import fuzs.forgeconfigapiport.impl.config.ForgeConfigPathsImpl;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

/**
 * Access to paths where different kinds of config files are stored by Forge.
 */
public interface ForgeConfigPaths {
    /**
     * implementation instance for retrieving config paths
     */
    ForgeConfigPaths INSTANCE = new ForgeConfigPathsImpl();

    /**
     * The directory where client configs are stored at.
     *
     * @return client config path
     */
    Path getClientConfigPath();

    /**
     * The directory where common configs are stored at.
     *
     * @return common config path
     */
    Path getCommonConfigPath();

    /**
     * The directory where server configs are stored at. By default, this is inside the world directory instead of the common <code>.minecraft/config/</code> directory.
     * <p>Forge Config Api Port has a config setting (which is not present in Forge itself) to load server configs from the common directory instead.
     * This method will always provide the directory that is currently set via the Forge Config Api Port config.
     *
     * @param server the current {@link MinecraftServer}
     * @return server config path
     */
    Path getServerConfigPath(final MinecraftServer server);

    /**
     * Path where default configs are stored, by default inside of <code>.minecraft/defaultconfigs</code>.
     * <p>This path is configurable via the Forge Config Api Port config.
     * <p>Default configs are mainly intended for server configs to allow pre-configured configs to be applied to every
     * newly created world (since server configs are handled per world). This mechanic also applies to other config types (although they are only created once).
     *
     * @return default configs path
     */
    Path getDefaultConfigsPath();
}