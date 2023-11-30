/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.impl.network.client.config;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.network.client.NetworkHooks;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.config.ConfigTracker;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

// Forge Config API Port: a class by that name does not exist on Forge, though this is an extract from net.minecraftforge.network.HandshakeHandler
// with the client side code regarding configs
public final class ConfigSyncClient {

    private ConfigSyncClient() {

    }

    public static CompletableFuture<@Nullable FriendlyByteBuf> onSyncConfigs(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf) {
        final String fileName = receiveSyncedConfig(buf);
        ForgeConfigAPIPort.LOGGER.debug("Received config sync for {} from server", fileName);
        FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
        response.writeUtf(fileName);
        ForgeConfigAPIPort.LOGGER.debug("Sent config sync for {} to server", fileName);
        return CompletableFuture.completedFuture(response);
    }

    public static CompletableFuture<@Nullable FriendlyByteBuf> onEstablishModdedConnection(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf) {
        ForgeConfigAPIPort.LOGGER.debug("Received modded connection marker from server");
        NetworkHooks.setModdedConnection();
        return CompletableFuture.completedFuture(new FriendlyByteBuf(Unpooled.buffer()));
    }

    private static String receiveSyncedConfig(final FriendlyByteBuf buf) {
        String fileName = buf.readUtf(32767);
        byte[] fileData = buf.readByteArray();
        if (!Minecraft.getInstance().isLocalServer()) {
            Optional.ofNullable(ConfigTracker.INSTANCE.fileMap().get(fileName)).ifPresent(config -> config.acceptSyncedConfig(fileData));
            Optional.ofNullable(net.neoforged.fml.config.ConfigTracker.INSTANCE.fileMap().get(fileName)).ifPresent(config -> config.acceptSyncedConfig(fileData));
        }
        return fileName;
    }
}