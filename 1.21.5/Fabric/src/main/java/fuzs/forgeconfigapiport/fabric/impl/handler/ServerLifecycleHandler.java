package fuzs.forgeconfigapiport.fabric.impl.handler;

import com.electronwill.nightconfig.core.file.FileWatcher;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerLifecycleHandler {
    public static final ResourceLocation BEFORE_PHASE = ForgeConfigAPIPort.id("before");
    public static final ResourceLocation AFTER_PHASE = ForgeConfigAPIPort.id("after");
    private static final LevelResource SERVER_CONFIG_LEVEL_RESOURCE = new LevelResource("serverconfig");

    public static void onServerStarting(MinecraftServer minecraftServer) {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER,
                FabricLoader.getInstance().getConfigDir(),
                getServerConfigPath(minecraftServer));
    }

    public static void onServerStopped(MinecraftServer minecraftServer) {
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER);
        // this thread leads to dedicated servers hanging, this seems to work for that for now
        // clients are fine, they don't hang because of it
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            FileWatcher.defaultInstance().stop();
        }
    }

    // Copied net.neoforged.neoforge.server.ServerLifecycleHooks
    private static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVER_CONFIG_LEVEL_RESOURCE);
        if (!Files.isDirectory(serverConfig)) {
            try {
                Files.createDirectories(serverConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final Path explanation = serverConfig.resolve("readme.txt");
        if (!Files.exists(explanation)) {
            try {
                Files.writeString(explanation, """
                                               Any server configs put in this folder will override the corresponding server config from <instance path>/config/<config path>.
                                               If the config being transferred is in a subfolder of the base config folder make sure to include that folder here in the path to the file you are overwriting.
                                               For example if you are overwriting a config with the path <instance path>/config/ExampleMod/config-server.toml, you would need to put it in serverconfig/ExampleMod/config-server.toml
                                               """, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfig;
    }
}
