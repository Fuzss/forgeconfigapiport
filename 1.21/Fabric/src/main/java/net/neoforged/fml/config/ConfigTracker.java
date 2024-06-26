/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class ConfigTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");
    public static final ConfigTracker INSTANCE = new ConfigTracker();
    private final ConcurrentHashMap<String, ModConfig> fileMap;
    private final EnumMap<ModConfig.Type, Set<ModConfig>> configSets;
    // Forge Config API Port: store a collection of mod configs since mods with multiple configs for the same type are supported
    private final ConcurrentHashMap<String, Map<ModConfig.Type, Collection<ModConfig>>> configsByMod;

    private ConfigTracker() {
        this.fileMap = new ConcurrentHashMap<>();
        this.configSets = new EnumMap<>(ModConfig.Type.class);
        this.configsByMod = new ConcurrentHashMap<>();
        this.configSets.put(ModConfig.Type.CLIENT, Collections.synchronizedSet(new LinkedHashSet<>()));
        this.configSets.put(ModConfig.Type.COMMON, Collections.synchronizedSet(new LinkedHashSet<>()));
        this.configSets.put(ModConfig.Type.SERVER, Collections.synchronizedSet(new LinkedHashSet<>()));
        this.configSets.put(ModConfig.Type.STARTUP, Collections.synchronizedSet(new LinkedHashSet<>()));
    }

    void trackConfig(final ModConfig config) {
        // Forge Config API Port: also check for duplicates in Forge config system, will cause issues otherwise during server config syncing
        if (this.fileMap.containsKey(config.getFileName()) || net.minecraftforge.fml.config.ConfigTracker.INSTANCE.fileMap().containsKey(config.getFileName())) {
            LOGGER.error(CONFIG,"Detected config file conflict {} between {} and {}", config.getFileName(), this.fileMap.get(config.getFileName()).getModId(), config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }
        this.fileMap.put(config.getFileName(), config);
        this.configSets.get(config.getType()).add(config);
        // Forge Config API Port: store a collection of mod configs since mods with multiple configs for the same type are supported
        this.configsByMod.computeIfAbsent(config.getModId(), (k)->new EnumMap<>(ModConfig.Type.class)).computeIfAbsent(config.getType(), type -> new ArrayList<>()).add(config);
        LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
        // Forge Config API Port: load configs immediately
        // unlike on forge there isn't really more than one loading stage for mods on fabric, therefore we load configs immediately
        // server configs are not handled here, they are all loaded at once when a world is loaded
        if (config.getType() != ModConfig.Type.SERVER) {
            this.openConfig(config, FabricLoader.getInstance().getConfigDir(), null);
        }
    }

    public void loadConfigs(ModConfig.Type type, Path configBasePath) {
        this.loadConfigs(type, configBasePath, null);
    }

    public void loadConfigs(ModConfig.Type type, Path configBasePath, @Nullable Path configOverrideBasePath) {
        LOGGER.debug(CONFIG, "Loading configs type {}", type);
        this.configSets.get(type).forEach(config -> this.openConfig(config, configBasePath, configOverrideBasePath));
    }

    public void unloadConfigs(ModConfig.Type type) {
        LOGGER.debug(CONFIG, "Unloading configs type {}", type);
        this.configSets.get(type).forEach(this::closeConfig);
    }

    private Path resolveBasePath(ModConfig config, Path configBasePath, @Nullable Path configOverrideBasePath) {
        if (configOverrideBasePath != null) {
            Path overrideFilePath = configOverrideBasePath.resolve(config.getFileName());
            if (Files.exists(overrideFilePath)) {
                LOGGER.info(CONFIG, "Found config file override in path {}", overrideFilePath);
                return configOverrideBasePath;
            }
        }
        return configBasePath;
    }

    public void openConfig(final ModConfig config, final Path configBasePath, @Nullable Path configOverrideBasePath) {
        LOGGER.trace(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
        final Path basePath = this.resolveBasePath(config, configBasePath, configOverrideBasePath);
        final CommentedFileConfig configData = ConfigFileTypeHandler.TOML.reader(basePath).apply(config);
        config.setConfigData(configData);
        // Forge Config API Port: invoke Fabric style callback instead of Forge event
        NeoForgeModConfigEvents.loading(config.getModId()).invoker().onModConfigLoading(config);
        config.save();
    }

    private void closeConfig(final ModConfig config) {
        if (config.getConfigData() != null) {
            LOGGER.trace(CONFIG, "Closing config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
            // stop the filewatcher before we save the file and close it, so reload doesn't fire
            ConfigFileTypeHandler.TOML.unload(config);
            // Forge Config API Port: invoke Fabric style callback instead of Forge event
            NeoForgeModConfigEvents.unloading(config.getModId()).invoker().onModConfigUnloading(config);
            config.save();
            config.setConfigData(null);
        }
    }

    public void loadDefaultServerConfigs() {
        this.configSets.get(ModConfig.Type.SERVER).forEach(modConfig -> {
            final CommentedConfig commentedConfig = CommentedConfig.inMemory();
            modConfig.getSpec().correct(commentedConfig);
            modConfig.setConfigData(commentedConfig);
            // Forge Config API Port: invoke Fabric style callback instead of Forge event
            NeoForgeModConfigEvents.loading(modConfig.getModId()).invoker().onModConfigLoading(modConfig);
        });
    }

    @Nullable
    public String getConfigFileName(String modId, ModConfig.Type type) {
        // Forge Config API Port: support mods with multiple configs for the same type
        List<String> fileNames = this.getConfigFileNames(modId, type);
        return fileNames.isEmpty() ? null : fileNames.getFirst();
    }

    // Forge Config API Port: support mods with multiple configs for the same type
    @ApiStatus.Experimental
    public List<String> getConfigFileNames(String modId, ModConfig.Type type) {
        return Optional.ofNullable(this.configsByMod.get(modId))
                .map(map -> map.get(type))
                .map(configs -> configs.stream()
                        .filter(config -> config.getConfigData() instanceof FileConfig)
                        .map(ModConfig::getFullPath)
                        .map(Object::toString)
                        .toList())
                .orElse(List.of());
    }

    public Map<ModConfig.Type, Set<ModConfig>> configSets() {
        return this.configSets;
    }

    public ConcurrentHashMap<String, ModConfig> fileMap() {
        return this.fileMap;
    }
}