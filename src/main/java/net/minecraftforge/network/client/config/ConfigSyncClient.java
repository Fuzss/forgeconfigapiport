/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.client.config;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.network.client.NetworkHooks;
import net.minecraftforge.network.config.ConfigSync;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraftforge.network.config.ConfigSync.FMLHSMARKER;

// Forge Config API Port: a class by that name does not exist on Forge, though this is an extract from net.minecraftforge.network.HandshakeHandler
// with the client side code regarding configs
public class ConfigSyncClient {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ConfigSyncClient INSTANCE = new ConfigSyncClient(ConfigTracker.INSTANCE);
    private final ConfigTracker tracker;

    private ConfigSyncClient(final ConfigTracker tracker) {
        this.tracker = tracker;
    }

    public void clientInit() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (client, handler, buf, listenerAdder) -> {
            final String fileName = this.receiveSyncedConfig(buf);
            LOGGER.debug(FMLHSMARKER, "Received config sync for {} from server", fileName);
            FriendlyByteBuf response = PacketByteBufs.create();
            response.writeUtf(fileName);
            LOGGER.debug(FMLHSMARKER, "Sent config sync for {} to server", fileName);
            return CompletableFuture.completedFuture(response);
        });
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.MODDED_CONNECTION_CHANNEL, (client, handler, buf, listenerAdder) -> {
            LOGGER.debug(FMLHSMARKER, "Received modded connection marker from server");
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