/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.impl.network.client.config;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.config.ConfigTracker;
import fuzs.forgeconfigapiport.impl.network.client.NetworkHooks;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

// Forge Config API Port: a class by that name does not exist on Forge, though this is an extract from net.minecraftforge.network.HandshakeHandler
// with the client side code regarding configs
public class ConfigSyncClient {
    public static final ConfigSyncClient INSTANCE = new ConfigSyncClient(ConfigTracker.INSTANCE);

    private final ConfigTracker tracker;

    private ConfigSyncClient(final ConfigTracker tracker) {
        this.tracker = tracker;
    }

    public void clientInit() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (client, handler, buf, listenerAdder) -> {
            final String fileName = this.receiveSyncedConfig(buf);
            ForgeConfigAPIPort.LOGGER.debug("Received config sync for {} from server", fileName);
            FriendlyByteBuf response = PacketByteBufs.create();
            response.writeUtf(fileName);
            ForgeConfigAPIPort.LOGGER.debug("Sent config sync for {} to server", fileName);
            return CompletableFuture.completedFuture(response);
        });
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.MODDED_CONNECTION_CHANNEL, (client, handler, buf, listenerAdder) -> {
            ForgeConfigAPIPort.LOGGER.debug("Received modded connection marker from server");
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