package fuzs.forgeconfigapiport.impl;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.base.api.event.Event;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerLoginNetworking;

import java.util.List;

public class ForgeConfigAPIPortQuilt implements ModInitializer {
    private static final ResourceLocation BEFORE_PHASE = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "before");
    private static final ResourceLocation AFTER_PHASE = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "after");

    @Override
    public void onInitialize(ModContainer mod) {
        registerMessages();
        registerHandlers();
    }

    private static void registerMessages() {
        ServerLoginConnectionEvents.QUERY_START.register((handler2, server2, sender, synchronizer2) -> {
            final List<Pair<String, FriendlyByteBuf>> pairs = ConfigSync.onSyncConfigs();
            for (Pair<String, FriendlyByteBuf> pair : pairs) {
                synchronizer2.waitFor(server2.submit(() -> sender.sendPacket(ConfigSync.SYNC_CONFIGS_CHANNEL, pair.getValue())));
            }
            synchronizer2.waitFor(server2.submit(() -> sender.sendPacket(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, PacketByteBufs.create())));
        });
        ServerLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (server1, handler1, understood1, buf1, synchronizer1, responseSender1) -> ConfigSync.onSyncConfigs(server1, handler1, understood1, buf1));
        ServerLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (server, handler, understood, buf, synchronizer, responseSender) -> ConfigSync.onEstablishModdedConnection(server, handler, understood, buf));
    }

    private static void registerHandlers() {
        ServerLifecycleEvents.STARTING.addPhaseOrdering(BEFORE_PHASE, Event.DEFAULT_PHASE);
        ServerLifecycleEvents.STARTING.register(BEFORE_PHASE, server -> {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server));
        });
        ServerLifecycleEvents.STOPPED.addPhaseOrdering(Event.DEFAULT_PHASE, AFTER_PHASE);
        ServerLifecycleEvents.STOPPED.register(AFTER_PHASE, server -> {
            ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server));
        });
    }
}
