package fuzs.forgeconfigapiport.fabric.impl.config.legacy;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.fabric.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.fabric.impl.handler.ServerLifecycleHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public final class ForgeConfigPathsV2Impl implements ForgeConfigPaths {

    @Override
    public Path getClientConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getCommonConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getServerConfigDirectory(final MinecraftServer server) {
        return ServerLifecycleHandler.getServerConfigPath(server);
    }

    @Override
    public boolean forceGlobalServerConfigs() {
        return ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("forceGlobalServerConfigs");
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        return fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths.INSTANCE.getDefaultConfigsDirectory();
    }
}
