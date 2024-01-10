/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.network.client;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.minecraft.network.Connection;
import net.minecraftforge.fml.config.ConfigTracker;

// Forge Config API Port: class greatly reduced to only contain code related to configs
public class NetworkHooks {
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
            ForgeConfigAPIPort.LOGGER.debug("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
            net.neoforged.fml.config.ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            // reset for next server
            setVanillaConnection();
            ForgeConfigAPIPort.LOGGER.debug("Connected to a modded server.");
        }
    }
}
