package fuzs.forgeconfigapiport.fabric.impl;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.impl.handler.ServerLifecycleHandler;
import fuzs.forgeconfigapiport.fabric.impl.network.config.ConfigSync;
import fuzs.forgeconfigapiport.impl.CommonAbstractions;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ForgeConfigAPIPortFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        registerMessages();
        registerHandlers();
        if (CommonAbstractions.isDevelopmentEnvironment() && CommonAbstractions.includeTestConfigs()) {
            NeoForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID, ModConfig.Type.SERVER, new ModConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build(), "forgeconfigapiport-server-neoforge.toml");
            ForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID, net.minecraftforge.fml.config.ModConfig.Type.SERVER, new ForgeConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build(), "forgeconfigapiport-server-forge.toml");
        }
    }

    private static void registerMessages() {
        ServerLoginConnectionEvents.QUERY_START.register((handler2, server2, sender, synchronizer2) -> {
            final List<Pair<String, FriendlyByteBuf>> pairs = ConfigSync.writeSyncedConfigs();
            for (Pair<String, FriendlyByteBuf> pair : pairs) {
                synchronizer2.waitFor(server2.submit(() -> sender.sendPacket(ConfigSync.SYNC_CONFIGS_CHANNEL, pair.getValue())));
            }
            synchronizer2.waitFor(server2.submit(() -> sender.sendPacket(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, PacketByteBufs.create())));
        });
        ServerLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (server1, handler1, understood1, buf1, synchronizer1, responseSender1) -> {
            ConfigSync.onSyncConfigs(server1, handler1, understood1, buf1);
        });
        ServerLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (server, handler, understood, buf, synchronizer, responseSender) -> {
            ConfigSync.onEstablishModdedConnection(server, handler, understood, buf);
        });
    }

    private static void registerHandlers() {
        ServerLifecycleEvents.SERVER_STARTING.addPhaseOrdering(ServerLifecycleHandler.BEFORE_PHASE, Event.DEFAULT_PHASE);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleHandler.BEFORE_PHASE, ServerLifecycleHandler::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.addPhaseOrdering(Event.DEFAULT_PHASE, ServerLifecycleHandler.AFTER_PHASE);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleHandler.AFTER_PHASE, ServerLifecycleHandler::onServerStopped);
    }
}
