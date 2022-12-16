/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.impl.network.config;

import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Forge Config API Port: this class is greatly altered from Forge, as it includes all relevant parts from net.minecraftforge.network.HandshakeHandler which has not been ported separately
// also there are a number of changes to adapt to Fabric's style of networking
public class ConfigSync {
    static final Marker NETWORK = MarkerFactory.getMarker("FMLNETWORK");
    public static final Marker FMLHSMARKER = Util.make(MarkerFactory.getMarker("FMLHANDSHAKE"), marker -> marker.add(NETWORK));
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation SYNC_CONFIGS_CHANNEL = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "sync_configs");
    public static final ResourceLocation MODDED_CONNECTION_CHANNEL = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "modded_connection");

    public static final ConfigSync INSTANCE = new ConfigSync(ConfigTracker.INSTANCE);
    private final ConfigTracker tracker;

    private ConfigSync(final ConfigTracker tracker) {
        this.tracker = tracker;
    }

    public void init() {
        ServerLoginConnectionEvents.QUERY_START.register((ServerLoginPacketListenerImpl handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) -> {
            final List<Pair<String, FriendlyByteBuf>> pairs = this.syncConfigs();
            for (Pair<String, FriendlyByteBuf> pair : pairs) {
                synchronizer.waitFor(server.submit(() -> sender.sendPacket(SYNC_CONFIGS_CHANNEL, pair.getValue())));
            }
            synchronizer.waitFor(server.submit(() -> sender.sendPacket(MODDED_CONNECTION_CHANNEL, PacketByteBufs.create())));
        });
        ServerLoginNetworking.registerGlobalReceiver(SYNC_CONFIGS_CHANNEL, (MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) -> {
            if (!understood) {
                // The client is likely a vanilla client.
                return;
            }
            String fileName = buf.readUtf(32767);
            LOGGER.debug(FMLHSMARKER, "Received acknowledgement for config sync for {} from client", fileName);
        });
        ServerLoginNetworking.registerGlobalReceiver(MODDED_CONNECTION_CHANNEL, (MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) -> {
            LOGGER.debug(FMLHSMARKER, "Received acknowledgement for modded connection marker from client");
        });
    }

    private List<Pair<String, FriendlyByteBuf>> syncConfigs() {
        final Map<String, byte[]> configData = tracker.configSets().get(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, config -> {
            try {
                return Files.readAllBytes(config.getFullPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        return configData.entrySet().stream().map(e -> {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUtf(e.getKey());
            buf.writeByteArray(e.getValue());
            return Pair.of("Config " + e.getKey(), buf);
        }).collect(Collectors.toList());
    }

    public void unloadSyncedConfig() {
        tracker.configSets().get(ModConfig.Type.SERVER).forEach(config -> config.acceptSyncedConfig(null));
    }
}