package fuzs.forgeconfigapiport.fabric.impl.config.legacy;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths;
import fuzs.forgeconfigapiport.fabric.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.fabric.impl.OtherCommonAbstractions;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class ForgeConfigPathsV3Impl implements ForgeConfigPaths {

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        return OtherCommonAbstractions.getGameDirectory().resolve(ForgeConfigApiPortConfig.INSTANCE.<String>getValue("defaultConfigsPath"));
    }
}
