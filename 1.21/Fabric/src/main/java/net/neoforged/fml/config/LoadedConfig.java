/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import fuzs.forgeconfigapiport.fabric.impl.core.ModConfigEventsHelper;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

record LoadedConfig(CommentedConfig config, @Nullable Path path, ModConfig modConfig) implements IConfigSpec.ILoadedConfig {
    @Override
    public void save() {
        if (path != null) {
            ConfigTracker.writeConfig(path, config);
        }
        modConfig.lock.lock();
        try {
            // Forge Config Api Port: invoke Fabric style callback instead of Forge event
            ModConfigEventsHelper.reloading().accept(modConfig);
        } finally {
            modConfig.lock.unlock();
        }
    }
}
