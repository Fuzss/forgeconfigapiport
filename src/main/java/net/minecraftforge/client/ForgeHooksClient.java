/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.network.config.ConfigSync;

// Forge Config API Port: class greatly reduced to only contain code related to configs
public class ForgeHooksClient {

    public static void handleClientLevelClosing(ClientLevel level)
    {
        ConfigSync.INSTANCE.unloadSyncedConfig();
    }
}
