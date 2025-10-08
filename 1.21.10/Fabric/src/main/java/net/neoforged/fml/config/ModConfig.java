/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public final class ModConfig {
    private final Type type;
    private final IConfigSpec spec;
    private final String fileName;
    // Forge Config Api Port: replace ModContainer with mod id
    private final String modId;
    @Nullable
    LoadedConfig loadedConfig;
    /**
     * NightConfig's own configs are threadsafe, but mod code is not necessarily.
     * This lock is used to prevent multiple concurrent config reloads or event dispatches.
     */
    final Lock lock;

    // Forge Config Api Port: replace ModContainer with mod id
    ModConfig(Type type, IConfigSpec spec, String modId, String fileName, ReentrantLock lock) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        // Forge Config Api Port: replace ModContainer with mod id, also additional check mod exists
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new IllegalArgumentException("No mod with id '%s'".formatted(modId));
        }
        this.modId = modId;
        this.lock = lock;
    }

    public Type getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public IConfigSpec getSpec() {
        return spec;
    }

    public String getModId() {
        // Forge Config Api Port: replace ModContainer with mod id
        return this.modId;
    }

    /**
     * Retrieve the currently loaded config, for direct manipulation of the underlying {@link CommentedConfig}.
     * Note that the config will change on reloads, and will be {@code null} when the config is not loaded.
     */
    @Nullable
    public IConfigSpec.ILoadedConfig getLoadedConfig() {
        return loadedConfig;
    }

    /**
     * Get the file path of the currently loaded config.
     *
     * @return The config's path. Will return {@code null} if the config is not currently loaded, or was loaded
     *         from memory rather than from a file.
     */
    @Nullable
    public Path getFullPath() {
        return loadedConfig != null ? loadedConfig.path() : null;
    }

    // Forge Config Api Port: adapt event constructor for Fabric style callback instead of Forge event
    void setConfig(@Nullable LoadedConfig loadedConfig, Consumer<ModConfig> eventConstructor) {
        lock.lock();

        try {
            this.loadedConfig = loadedConfig;
            spec.acceptConfig(loadedConfig);
            // Forge Config Api Port: invoke Fabric style callback instead of Forge event
            eventConstructor.accept(this);
        } finally {
            lock.unlock();
        }
    }

    // Forge Config Api Port: implement StringRepresentable to allow using vanilla argument type
    public enum Type implements StringRepresentable {
        /**
         * Common mod config for configuration that needs to be loaded on both environments.
         * Loaded on both servers and clients.
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-common" by default.
         */
        COMMON,
        /**
         * Client config is for configuration affecting the ONLY client state such as graphical options.
         * Only loaded on the client side.
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-client" by default.
         */
        CLIENT,
        /**
         * Server type config is configuration that is associated with a server instance.
         * Only loaded during server startup.
         * Stored in a server/save specific "serverconfig" directory.
         * Synced to clients during connection.
         * Suffix is "-server" by default.
         */
        SERVER,
        /**
         * Startup configs are for configurations that need to run as early as possible.
         * Loaded as soon as the config is registered to FML.
         * Please be aware when using them, as using these configs to enable/disable registration and anything that must be present on both sides
         * can cause clients to have issues connecting to servers with different config values.
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-startup" by default.
         */
        STARTUP;

        public String extension() {
            // Forge Config Api Port: replace NeoForge helper class method call
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName() {
            return this.name();
        }
    }
}
