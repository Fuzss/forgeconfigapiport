package net.minecraftforge.api;

/**
 * Exposes important paths for config types.
 *
 * @deprecated Greatly expanded and moved to {@link fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths}.
 */
@Deprecated(forRemoval = true)
public final class ConfigPaths {
    /**
     * path of the server config directory inside each world dir
     */
    public static final String SERVER_CONFIG_PATH = "serverconfig";
    /**
     * path of the default configs directory inside the main minecraft dir
     */
    public static final String DEFAULT_CONFIGS_PATH = "defaultconfigs";
}
