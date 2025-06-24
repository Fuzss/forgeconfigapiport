/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.network;

import fuzs.forgeconfigapiport.fabric.impl.network.payload.ConfigFilePayload;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class ConfigSync {

    public static List<ConfigFilePayload> syncConfigs() {
        final Map<String, byte[]> configData = ModConfigs.getConfigSet(ModConfig.Type.SERVER)
                .stream()
                .collect(Collectors.toMap(ModConfig::getFileName, mc -> {
                    try {
                        return Files.readAllBytes(mc.getFullPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));

        return configData.entrySet().stream().map(e -> new ConfigFilePayload(e.getKey(), e.getValue())).toList();
    }

    public static void receiveSyncedConfig(final byte[] contents, final String fileName) {
        Optional.ofNullable(ModConfigs.getFileMap().get(fileName))
                .ifPresent(mc -> ConfigTracker.acceptSyncedConfig(mc, contents));
    }

    // Forge Config API Port: custom method similar to NeoForge's NetworkRegistry::initializeNonModdedConnection
    public static void handleClientLoginSuccess() {
        if (ClientConfigurationNetworking.canSend(ConfigFilePayload.TYPE)) {
            ForgeConfigAPIPort.LOGGER.debug("Connected to a modded server.");
        } else {
            ForgeConfigAPIPort.LOGGER.debug("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        }
    }
}
