package net.minecraftforge.fmllegacy.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.ConfigPaths;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.mixin.accessor.LevelResourceAccessor;

import java.nio.file.Path;

public class ServerLifecycleHooks {
    private static final LevelResource SERVERCONFIG = LevelResourceAccessor.create(ConfigPaths.SERVER_CONFIG_PATH);

    private static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        FileUtils.getOrCreateDirectory(serverConfig, "server config directory");
        return serverConfig;
    }

    public static void handleServerAboutToStart(final MinecraftServer server) {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
    }

    public static void handleServerStopped(final MinecraftServer server) {
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
    }
}
