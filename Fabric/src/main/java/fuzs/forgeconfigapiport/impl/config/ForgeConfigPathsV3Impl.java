package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths;
import fuzs.forgeconfigapiport.impl.core.CommonAbstractions;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class ForgeConfigPathsV3Impl implements ForgeConfigPaths {

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        return CommonAbstractions.INSTANCE.getGameDirectory().resolve(ForgeConfigApiPortConfig.INSTANCE.<String>getValue("defaultConfigsPath"));
    }
}
