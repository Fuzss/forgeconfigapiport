/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.network;

import fuzs.forgeconfigapiport.fabric.impl.network.payload.ConfigFilePayload;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
public class ConfigSync {
    // Forge Config API Port: marker field for catching up missing server configs if necessary
    private static boolean isVanillaConnection = true;

    public static List<ConfigFilePayload> syncConfigs() {
        final Map<String, byte[]> neoForgeConfigData = ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
            try {
                return Files.readAllBytes(mc.getFullPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        final Map<String, byte[]> forgeConfigData = net.minecraftforge.fml.config.ConfigTracker.INSTANCE.configSets().get(
                net.minecraftforge.fml.config.ModConfig.Type.SERVER).stream().collect(Collectors.toMap(
                net.minecraftforge.fml.config.ModConfig::getFileName, mc -> {
            try {
                return Files.readAllBytes(mc.getFullPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        return Stream.concat(neoForgeConfigData.entrySet().stream(), forgeConfigData.entrySet().stream())
                .map(e -> new ConfigFilePayload(e.getKey(), e.getValue()))
                .toList();
    }

    public static void receiveSyncedConfig(final byte[] contents, final String fileName) {
        // Forge Config API Port: invoke this here as an easy way to tell that we are connected to as server that has sent the server configs
        onEstablishModdedConnection();
        if (!Minecraft.getInstance().isLocalServer()) {
            // we just check for the config on both Forge & NeoForge systems, we make sure during config registration that no duplicates across config systems are allowed
            Optional.ofNullable(ConfigTracker.INSTANCE.fileMap().get(fileName)).ifPresent(mc -> mc.acceptSyncedConfig(contents));
            Optional.ofNullable(net.minecraftforge.fml.config.ConfigTracker.INSTANCE.fileMap().get(fileName)).ifPresent(mc -> mc.acceptSyncedConfig(contents));
        }
        // Forge Config API Port: log received config
        ForgeConfigAPIPort.LOGGER.debug("Received config sync for {} from server", fileName);
    }

    // Forge Config API Port: custom method to identify a modded server
    private static void onEstablishModdedConnection() {
        isVanillaConnection = false;
        ForgeConfigAPIPort.LOGGER.debug("Received modded connection marker from server");
    }

    // Forge Config API Port: custom method similar to NeoForge's NetworkRegistry::initializeNonModdedConnection
    public static void handleClientLoginSuccess() {
        if (isVanillaConnection) {
            ForgeConfigAPIPort.LOGGER.debug("Connected to a vanilla server. Catching up missing behaviour.");
            net.minecraftforge.fml.config.ConfigTracker.INSTANCE.loadDefaultServerConfigs();
            net.neoforged.fml.config.ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            // reset for next server
            isVanillaConnection = true;
            ForgeConfigAPIPort.LOGGER.debug("Connected to a modded server.");
        }
    }
}
