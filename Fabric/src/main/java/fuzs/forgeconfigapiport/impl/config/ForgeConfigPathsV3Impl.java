package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.mixin.accessor.LevelResourceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ForgeConfigPathsV3Impl implements ForgeConfigPaths {
    private static final LevelResource SERVER_CONFIG_LEVEL_RESOURCE = LevelResourceAccessor.forgeconfigapiport$create("serverconfig");

    // Copied from net.minecraftforge.fml.loading.FileUtils
    private static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of " + dirLabel);
        }
        if (!Files.isDirectory(dirPath)) {
            ForgeConfigAPIPort.LOGGER.debug("Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    ForgeConfigAPIPort.LOGGER.error("Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    ForgeConfigAPIPort.LOGGER.error("Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            ForgeConfigAPIPort.LOGGER.debug("Created {} directory : {}", dirLabel, dirPath);
        } else {
            ForgeConfigAPIPort.LOGGER.debug("Found existing {} directory : {}", dirLabel, dirPath);
        }
        return dirPath;
    }

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
        if (this.forceGlobalServerConfigs()) return FabricLoader.getInstance().getConfigDir();
        final Path serverConfig = server.getWorldPath(SERVER_CONFIG_LEVEL_RESOURCE);
        getOrCreateDirectory(serverConfig, "server config directory");
        return serverConfig;
    }

    @Override
    public boolean forceGlobalServerConfigs() {
        return ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("forceGlobalServerConfigs");
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        Path defaultConfigs = FabricLoader.getInstance().getGameDir().resolve(ForgeConfigApiPortConfig.INSTANCE.<String>getValue("defaultConfigsPath"));
        getOrCreateDirectory(defaultConfigs, "default configs directory");
        return defaultConfigs;
    }
}
