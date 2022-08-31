/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.ConfigPaths;
import org.slf4j.Logger;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

// Forge Config API Port: class greatly reduced to only contain code related to configs
public class FMLConfig
{
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * @deprecated renamed to {@link #load()}, you should have never used this anyways...
     */
    @Deprecated(forRemoval = true)
    public static void loadDefaultConfigPath()
    {
        load();
    }

    public static void load()
    {
        if (LOGGER.isTraceEnabled(CORE))
        {
            LOGGER.trace(CORE, "Default config paths at {}", FMLConfig.defaultConfigPath());
        }
        FileUtils.getOrCreateDirectory(FabricLoader.getInstance().getGameDir().resolve(defaultConfigPath()), "default config directory");
    }

    public static String defaultConfigPath()
    {
        return ConfigPaths.DEFAULT_CONFIGS_PATH;
    }
}
