/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * @deprecated Class is being removed in favor of using NeoForge's config system internally on Fabric.
 */
@Deprecated(forRemoval = true)
public class ModConfig {
    private final Type type;
    private final IConfigSpec<?> spec;
    private final String fileName;
    // Forge Config Api Port: replace ModContainer with mod id
    private final String modId;
    // Forge Config Api Port: whole class only kept for backwards compatibility, no longer supports operations
//    private final ConfigFileTypeHandler configHandler;
    // Forge Config Api Port: whole class only kept for backwards compatibility, no longer supports operations
//    private CommentedConfig configData;
    // Forge Config Api Port: add NeoForge loaded config instance for saving and config data access
    @ApiStatus.Internal
    @Nullable
    public net.neoforged.fml.config.IConfigSpec.ILoadedConfig loadedConfig;
//    private Callable<Void> saveHandler;
    // Forge Config Api Port: add NeoForge mod config instance for accessing ModConfig::getFullPath
    @Nullable
    private final net.neoforged.fml.config.ModConfig modConfig;

    // Forge Config Api Port: add NeoForge mod config instance
    @ApiStatus.Internal
    public ModConfig(final Type type, final IConfigSpec<?> spec, String modId, final String fileName, net.neoforged.fml.config.ModConfig modConfig) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        // Forge Config Api Port: replace ModContainer with mod id, also additional check mod exists
//        if (!FabricLoader.getInstance().isModLoaded(modId)) {
//            throw new IllegalArgumentException("No mod with id '%s'".formatted(modId));
//        }
        this.modId = modId;
        // Forge Config Api Port: whole class only kept for backwards compatibility, no longer supports operations
//        this.configHandler = ConfigFileTypeHandler.TOML;
//        ConfigTracker.INSTANCE.trackConfig(this);
        // Forge Config Api Port: add NeoForge mod config instance
        this.modConfig = modConfig;
    }

    // Forge Config Api Port: replace ModContainer with mod id, marked as internal for common project as no mod id constructor exists on Forge
    public ModConfig(final Type type, final IConfigSpec<?> spec, String modId, final String fileName) {
        // Forge Config Api Port: overload custom constructor that accepts the NeoForge mod config
        this(type, spec, modId, fileName, null);
    }

    // Forge Config Api Port: replace ModContainer with mod id, marked as internal for common project as no mod id constructor exists on Forge
    public ModConfig(final Type type, final IConfigSpec<?> spec, String modId) {
        this(type, spec, modId, defaultConfigName(type, modId));
    }

    static String defaultConfigName(Type type, String modId) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format("%s-%s.toml", modId, type.extension());
    }

    public Type getType() {
        return this.type;
    }

    public String getFileName() {
        return this.fileName;
    }

    // Forge Config Api Port: whole class only kept for backwards compatibility, no longer supports operations
//    public ConfigFileTypeHandler getHandler() {
//        return this.configHandler;
//    }

    @SuppressWarnings("unchecked")
    public <T extends IConfigSpec<T>> IConfigSpec<T> getSpec() {
        return (IConfigSpec<T>) this.spec;
    }

    public String getModId() {
        // Forge Config Api Port: replace ModContainer with mod id
        return this.modId;
    }

    public CommentedConfig getConfigData() {
        // Forge Config Api Port: config data field is unused, retrieve from loaded config instance instead
        return this.loadedConfig != null ? this.loadedConfig.config() : null;
//        return this.configData;
    }

    void setConfigData(final CommentedConfig configData) {
        // Forge Config Api Port: whole class only kept for backwards compatibility, no longer supports operations
        throw new UnsupportedOperationException();
//        this.configData = configData;
//        this.spec.acceptConfig(this.configData);
    }

    // Forge Config Api Port: not adapted for Fabric
//    void fireEvent(final IConfigEvent configEvent) {
//        this.container.dispatchConfigEvent(configEvent);
//    }

    public void save() {
        // Forge Config Api Port: support saving via loaded config instance, same behavior as Forge without a null-check
        Objects.requireNonNull(this.loadedConfig, "loaded config is null");
        this.loadedConfig.save();
//        ((CommentedFileConfig) this.configData).save();
    }

    public Path getFullPath() {
        // Forge Config Api Port: implementation no longer uses file config, updated with new path access
        Objects.requireNonNull(this.modConfig, "mod config is null");
        return this.modConfig.getFullPath();
//        return ((CommentedFileConfig) this.configData).getNioPath();
    }

    public void acceptSyncedConfig(byte[] bytes) {
        // Forge Config Api Port: whole class only kept for backwards compatibility, no longer supports operations
        throw new UnsupportedOperationException();
//        if (bytes != null) {
//            this.setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes)));
//            // Forge Config Api Port: invoke Fabric style callback instead of Forge event
//            ForgeModConfigEvents.reloading(this.getModId()).invoker().onModConfigReloading(this);
//        } else {
//            this.setConfigData(null);
//            // Forge Config Api Port: invoke Fabric style callback instead of Forge event
//            ForgeModConfigEvents.unloading(this.getModId()).invoker().onModConfigUnloading(this);
//        }
    }

    // Forge Config Api Port: implements StringRepresentable to allow using vanilla argument type for /config
    /**
     * @deprecated Class is being removed in favor of using NeoForge's config system internally on Fabric.
     */
    @Deprecated(forRemoval = true)
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
//        /**
//         * Player type config is configuration that is associated with a player.
//         * Preferences around machine states, for example.
//         */
//        PLAYER,
        /**
         * Server type config is configuration that is associated with a server instance.
         * Only loaded during server startup.
         * Stored in a server/save specific "serverconfig" directory.
         * Synced to clients during connection.
         * Suffix is "-server" by default.
         */
        SERVER;

        public String extension() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        // Forge Config Api Port: implements StringRepresentable to allow using vanilla argument type for /config
        // It's ok to use this in a Fabric/Quilt project, just don't use it in Common, that's what the annotation is for
        @Override
        public String getSerializedName() {
            return this.extension();
        }
    }
}