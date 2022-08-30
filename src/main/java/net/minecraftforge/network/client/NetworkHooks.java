/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.client;

import com.mojang.logging.LogUtils;
import net.minecraft.network.Connection;
import net.minecraftforge.fml.config.ConfigTracker;
import org.slf4j.Logger;

// Forge Config API Port: class greatly reduced to only contain code related to configs
public class NetworkHooks {
    // Forge Config API Port: replace with SLF4J logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean isVanillaConnection = true;

    public static void setModdedConnection() {
        isVanillaConnection = false;
    }

    private static void setVanillaConnection() {
        isVanillaConnection = true;
    }

    public static boolean isVanillaConnection(Connection manager)
    {
        return isVanillaConnection;
    }

    public static void handleClientLoginSuccess(Connection manager) {
        if (isVanillaConnection(manager)) {
            LOGGER.info("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            // reset for next server
            setVanillaConnection();
            LOGGER.info("Connected to a modded server.");
        }
    }
}
