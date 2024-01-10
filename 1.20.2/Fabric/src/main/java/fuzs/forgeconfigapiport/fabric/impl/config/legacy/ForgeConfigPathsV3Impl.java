package fuzs.forgeconfigapiport.fabric.impl.config.legacy;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths;
import fuzs.forgeconfigapiport.fabric.impl.config.ForgeConfigApiPortConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class ForgeConfigPathsV3Impl implements ForgeConfigPaths {

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        return ForgeConfigApiPortConfig.getDefaultConfigsDirectory();
    }
}
