package fuzs.forgeconfigapiport.fabric.impl;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.impl.handler.ServerLifecycleHandler;
import fuzs.forgeconfigapiport.fabric.impl.network.ConfigSync;
import fuzs.forgeconfigapiport.fabric.impl.network.configuration.SyncConfig;
import fuzs.forgeconfigapiport.fabric.impl.network.payload.ConfigFilePayload;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ForgeConfigAPIPortFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        registerMessages();
        registerEventHandlers();
        setupDevelopmentEnvironment();
    }

    private static void registerMessages() {
        ServerConfigurationConnectionEvents.CONFIGURE.register((ServerConfigurationPacketListenerImpl handler, MinecraftServer server) -> {
            if (ServerConfigurationNetworking.canSend(handler, ConfigFilePayload.TYPE)) {
                handler.addTask(new SyncConfig(handler));
            }
        });
        PayloadTypeRegistry.configurationS2C().register(ConfigFilePayload.TYPE, ConfigFilePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigFilePayload.TYPE, ConfigFilePayload.STREAM_CODEC);
    }

    private static void registerEventHandlers() {
        ServerLifecycleEvents.SERVER_STARTING.addPhaseOrdering(ServerLifecycleHandler.BEFORE_PHASE,
                Event.DEFAULT_PHASE);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleHandler.BEFORE_PHASE,
                ServerLifecycleHandler::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.addPhaseOrdering(Event.DEFAULT_PHASE, ServerLifecycleHandler.AFTER_PHASE);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleHandler.AFTER_PHASE,
                ServerLifecycleHandler::onServerStopped);
        ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer server) -> {
            // Reset WORLD type config caches
            ModConfigs.getFileMap().values().forEach(config -> {
                if (config.getSpec() instanceof ModConfigSpec spec) {
                    spec.resetCaches(ModConfigSpec.RestartType.WORLD);
                }
            });
        });
        ServerTickEvents.END_SERVER_TICK.register(ConfigSync::syncPendingConfigs);
        ConfigSync.registerEventListeners();
    }

    private static void setupDevelopmentEnvironment() {
        if (CommonAbstractions.INSTANCE.isDevelopmentEnvironment(ForgeConfigAPIPort.MOD_ID)) {
            ConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID,
                    ModConfig.Type.SERVER,
                    new ModConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build(),
                    "forgeconfigapiport-server-neoforge.toml");
            ConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID,
                    ModConfig.Type.SERVER,
                    new ForgeConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build(),
                    "forgeconfigapiport-server-forge.toml");
        }
    }
}
