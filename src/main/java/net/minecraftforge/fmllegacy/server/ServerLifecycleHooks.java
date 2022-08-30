/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.ConfigPaths;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.mixin.accessor.LevelResourceAccessor;

import java.nio.file.Path;

// Forge Config API Port: class greatly reduced to only contain code related to configs
public class ServerLifecycleHooks {
    private static final LevelResource SERVERCONFIG = LevelResourceAccessor.create(ConfigPaths.SERVER_CONFIG_PATH);

    private static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        FileUtils.getOrCreateDirectory(serverConfig, "server config directory");
        return serverConfig;
    }

    public static void handleServerAboutToStart(final MinecraftServer server) {
        // Forge Config API Port: removed everything else which wasn't config related
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
    }

    public static void handleServerStopped(final MinecraftServer server) {
        // Forge Config API Port: removed everything else which wasn't config related
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
    }
}
