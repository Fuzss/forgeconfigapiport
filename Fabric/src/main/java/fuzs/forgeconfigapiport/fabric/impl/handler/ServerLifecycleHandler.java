package fuzs.forgeconfigapiport.fabric.impl.handler;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerLifecycleHandler {
    public static final ResourceLocation BEFORE_PHASE = ForgeConfigAPIPort.id("before");
    public static final ResourceLocation AFTER_PHASE = ForgeConfigAPIPort.id("after");
    private static final LevelResource SERVER_CONFIG_LEVEL_RESOURCE = new LevelResource("serverconfig");

    public static void onServerStarting(MinecraftServer server) {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
        net.neoforged.fml.config.ConfigTracker.INSTANCE.loadConfigs(net.neoforged.fml.config.ModConfig.Type.SERVER, getServerConfigPath(server));
    }

    public static void onServerStopped(MinecraftServer server) {
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
        net.neoforged.fml.config.ConfigTracker.INSTANCE.unloadConfigs(net.neoforged.fml.config.ModConfig.Type.SERVER, getServerConfigPath(server));
    }

    // Copied from net.minecraftforge.server.ServerLifecycleHooks
    public static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVER_CONFIG_LEVEL_RESOURCE);
        if (!Files.isDirectory(serverConfig)) {
            try {
                Files.createDirectories(serverConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfig;
    }
}
