package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public final class ForgeConfigPathsV2Impl implements ForgeConfigPaths {

    @Override
    public Path getClientConfigDirectory() {
        return fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths.INSTANCE.getClientConfigDirectory();
    }

    @Override
    public Path getCommonConfigDirectory() {
        return fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths.INSTANCE.getCommonConfigDirectory();
    }

    @Override
    public Path getServerConfigDirectory(final MinecraftServer server) {
        return fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server);
    }

    @Override
    public boolean forceGlobalServerConfigs() {
        return fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths.INSTANCE.forceGlobalServerConfigs();
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        return fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths.INSTANCE.getDefaultConfigsDirectory();
    }
}
