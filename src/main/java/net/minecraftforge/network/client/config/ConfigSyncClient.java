package net.minecraftforge.network.client.config;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.network.client.NetworkHooks;
import net.minecraftforge.network.config.ConfigSync;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ConfigSyncClient {
    public static final ConfigSyncClient INSTANCE = new ConfigSyncClient(ConfigTracker.INSTANCE);
    private final ConfigTracker tracker;

    private ConfigSyncClient(final ConfigTracker tracker) {
        this.tracker = tracker;
    }

    public void clientInit() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (client, handler, buf, listenerAdder) -> {
            final String fileName = this.receiveSyncedConfig(buf);
            ConfigSync.LOGGER.debug(ConfigSync.FMLHSMARKER, "Received config sync for {} from server", fileName);
            FriendlyByteBuf response = PacketByteBufs.create();
            response.writeUtf(fileName);
            ConfigSync.LOGGER.debug(ConfigSync.FMLHSMARKER, "Sent config sync for {} to server", fileName);
            return CompletableFuture.completedFuture(response);
        });
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.MODDED_CONNECTION_CHANNEL, (client, handler, buf, listenerAdder) -> {
            ConfigSync.LOGGER.debug(ConfigSync.FMLHSMARKER, "Received modded connection marker from server");
            NetworkHooks.setModdedConnection();
            return CompletableFuture.completedFuture(PacketByteBufs.create());
        });
    }

    private String receiveSyncedConfig(final FriendlyByteBuf buf) {
        String fileName = buf.readUtf(32767);
        byte[] fileData = buf.readByteArray();
        if (!Minecraft.getInstance().isLocalServer()) {
            Optional.ofNullable(this.tracker.fileMap().get(fileName)).ifPresent(config -> config.acceptSyncedConfig(fileData));
        }
        return fileName;
    }
}