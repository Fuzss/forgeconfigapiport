/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Locale;

public class ModConfig {
    private final Type type;
    private final IConfigSpec<?> spec;
    private final String fileName;
    // Forge Config API Port: replace ModContainer with mod id
    private final String modId;
    private CommentedConfig configData;

    // Forge Config API Port: replace ModContainer with mod id, marked as internal for common project as no mod id constructor exists on Forge
    @ApiStatus.Experimental
    public ModConfig(final Type type, final IConfigSpec<?> spec, String modId, final String fileName) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        // Forge Config API Port: replace ModContainer with mod id, also additional check mod exists
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new IllegalArgumentException("No mod with id '%s'".formatted(modId));
        }
        this.modId = modId;
        ConfigTracker.INSTANCE.trackConfig(this);
    }

    // Forge Config API Port: replace ModContainer with mod id, marked as internal for common project as no mod id constructor exists on Forge
    @ApiStatus.Experimental
    public ModConfig(final Type type, final IConfigSpec<?> spec, String modId) {
        this(type, spec, modId, defaultConfigName(type, modId));
    }

    private static String defaultConfigName(Type type, String modId) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }

    public Type getType() {
        return this.type;
    }

    public String getFileName() {
        return this.fileName;
    }

    @SuppressWarnings("unchecked")
    public <T extends IConfigSpec<T>> IConfigSpec<T> getSpec() {
        return (IConfigSpec<T>) this.spec;
    }

    public String getModId() {
        // Forge Config API Port: replace ModContainer with mod id
        return this.modId;
    }

    public CommentedConfig getConfigData() {
        return this.configData;
    }

    void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.spec.acceptConfig(this.configData);
    }

    public void save() {
        ((CommentedFileConfig) this.configData).save();
    }

    public Path getFullPath() {
        return ((CommentedFileConfig) this.configData).getNioPath();
    }

    public void acceptSyncedConfig(byte[] bytes) {
        if (bytes != null) {
            this.setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes)));
            // Forge Config API Port: invoke Fabric style callback instead of Forge event
            NeoForgeModConfigEvents.reloading(this.getModId()).invoker().onModConfigReloading(this);
        } else {
            this.setConfigData(null);
            // Forge Config API Port: invoke Fabric style callback instead of Forge event
            NeoForgeModConfigEvents.unloading(this.getModId()).invoker().onModConfigUnloading(this);
        }
    }

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
            return this.name().toLowerCase(Locale.ROOT);
        }

        // Forge Config API Port: implements StringRepresentable to allow using vanilla argument type for /config
        // It's ok to use this in a Fabric/Quilt project, just don't use it in Common, that's what the annotation is for
        @Override
        public String getSerializedName() {
            return this.extension();
        }
    }
}