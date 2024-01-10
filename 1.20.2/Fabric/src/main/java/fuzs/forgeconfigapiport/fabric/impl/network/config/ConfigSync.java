/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.network.config;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Forge Config API Port: this class is greatly altered from Forge, as it includes all relevant parts from net.minecraftforge.network.HandshakeHandler which has not been ported separately
// also there are a number of changes to adapt to Fabric's style of networking
public final class ConfigSync {
    public static final ResourceLocation SYNC_CONFIGS_CHANNEL = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "sync_configs");
    public static final ResourceLocation ESTABLISH_MODDED_CONNECTION_CHANNEL = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "modded_connection");

    private ConfigSync() {

    }

    public static List<Pair<String, FriendlyByteBuf>> writeSyncedConfigs() {
        final Map<String, byte[]> configData = ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, config -> {
            try {
                return Files.readAllBytes(config.getFullPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        net.neoforged.fml.config.ConfigTracker.INSTANCE.configSets().get(net.neoforged.fml.config.ModConfig.Type.SERVER).forEach(config -> {
            try {
                configData.put(config.getFileName(), Files.readAllBytes(config.getFullPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return configData.entrySet().stream().map(entry -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeUtf(entry.getKey());
            buf.writeByteArray(entry.getValue());
            return Pair.of("Config " + entry.getKey(), buf);
        }).collect(Collectors.toList());
    }

    public static void onSyncConfigs(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf) {
        // The client is likely a vanilla client.
        if (!understood) return;
        ForgeConfigAPIPort.LOGGER.debug("Received acknowledgement for config sync for {} from client", buf.readUtf(32767));
    }

    public static void onEstablishModdedConnection(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf) {
        ForgeConfigAPIPort.LOGGER.debug("Received acknowledgement for modded connection marker from client");
    }

    public static void unloadSyncedConfig() {
        ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.SERVER).forEach(config -> config.acceptSyncedConfig(null));
        net.neoforged.fml.config.ConfigTracker.INSTANCE.configSets().get(net.neoforged.fml.config.ModConfig.Type.SERVER).forEach(config -> config.acceptSyncedConfig(null));
    }
}