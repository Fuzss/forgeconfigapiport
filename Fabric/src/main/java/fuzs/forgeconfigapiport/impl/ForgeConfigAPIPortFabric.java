package fuzs.forgeconfigapiport.impl;

import com.electronwill.nightconfig.core.file.FileWatcher;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;

public class ForgeConfigAPIPortFabric implements ModInitializer {
    private static final ResourceLocation BEFORE_PHASE = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "before");
    private static final ResourceLocation AFTER_PHASE = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "after");

    @Override
    public void onInitialize() {
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
        ServerLifecycleEvents.SERVER_STARTING.addPhaseOrdering(BEFORE_PHASE, Event.DEFAULT_PHASE);
        ServerLifecycleEvents.SERVER_STARTING.register(BEFORE_PHASE, server -> {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server));
        });
        ServerLifecycleEvents.SERVER_STOPPED.addPhaseOrdering(Event.DEFAULT_PHASE, AFTER_PHASE);
        ServerLifecycleEvents.SERVER_STOPPED.register(AFTER_PHASE, server -> {
            ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server));
            // this thread leads to dedicated servers hanging, this seems to work for that for now
            // clients are fine, they don't hang because of it
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                try {
                    FileWatcher.defaultInstance().stop();
                } catch (IOException exception) {
                    // NO-OP
                }
            }
        });
    }
}
