/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.fabric.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.fabric.impl.core.ModConfigEventsHelper;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * The configuration tracker manages various types of mod configurations.
 * Due to the parallel nature of mod initialization, modifying the configuration state must be <strong>thread-safe</strong>.
 */
@ApiStatus.Internal
public class ConfigTracker {
    public static final ConfigTracker INSTANCE = new ConfigTracker();
    static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");
    private static final Logger LOGGER = LogUtils.getLogger();
    // Forge Config Api Port: adapted for Fabric
    private static final Path defaultConfigPath = ForgeConfigApiPortConfig.getDefaultConfigsDirectory();

    final ConcurrentHashMap<String, ModConfig> fileMap = new ConcurrentHashMap<>();
    final EnumMap<ModConfig.Type, Set<ModConfig>> configSets = new EnumMap<>(ModConfig.Type.class);
    final ConcurrentHashMap<String, List<ModConfig>> configsByMod = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locksByMod = new ConcurrentHashMap<>();

    @VisibleForTesting
    public ConfigTracker() {
        for (var type : ModConfig.Type.values()) {
            this.configSets.put(type, Collections.synchronizedSet(new LinkedHashSet<>()));
        }
    }

    /**
     * Registers a new configuration of the given type for a mod, using the default filename for this type of config.
     * <p>
     * Registering a configuration is required to receive configuration events.
     */
    // Forge Config Api Port: replace ModContainer with mod id
    public ModConfig registerConfig(ModConfig.Type type, IConfigSpec spec, String modId) {
        return registerConfig(type, spec, modId, defaultConfigName(type, modId));
    }

    /**
     * Registers a new configuration of the given type for a mod, using a custom filename.
     * <p>
     * Registering a configuration is required to receive configuration events.
     */
    // Forge Config Api Port: replace ModContainer with mod id
    public ModConfig registerConfig(ModConfig.Type type, IConfigSpec spec, String modId, String fileName) {
        var lock = locksByMod.computeIfAbsent(modId, m -> new ReentrantLock());
        var modConfig = new ModConfig(type, spec, modId, fileName, lock);
        trackConfig(modConfig);

        // Forge Config Api Port: load all configs other than server immediately
        // Unlike on NeoForge there is no more than one loading stage for mods on Fabric, therefore we get no other change to load configs.
        if (modConfig.getType() != ModConfig.Type.SERVER) {
            openConfig(modConfig, FabricLoader.getInstance().getConfigDir(), null);
        }

        return modConfig;
    }

    private static String defaultConfigName(ModConfig.Type type, String modId) {
        // for mod-id "forge", config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }

    void trackConfig(ModConfig config) {
        var previousValue = this.fileMap.putIfAbsent(config.getFileName(), config);
        if (previousValue != null) {
            String errorMessage = "Detected config file conflict on %s from %s (already registered by %s)".formatted(config.getFileName(), config.getModId(), previousValue.getModId());
            LOGGER.error(CONFIG, errorMessage);
            throw new RuntimeException(errorMessage);
        }
        this.configSets.get(config.getType()).add(config);
        this.configsByMod.computeIfAbsent(config.getModId(), (k) -> Collections.synchronizedList(new ArrayList<>())).add(config);
        LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
    }

    public void loadConfigs(ModConfig.Type type, Path configBasePath) {
        loadConfigs(type, configBasePath, null);
    }

    public void loadConfigs(ModConfig.Type type, Path configBasePath, @Nullable Path configOverrideBasePath) {
        LOGGER.debug(CONFIG, "Loading configs type {}", type);
        this.configSets.get(type).forEach(config -> openConfig(config, configBasePath, configOverrideBasePath));
    }

    public void unloadConfigs(ModConfig.Type type) {
        LOGGER.debug(CONFIG, "Unloading configs type {}", type);
        this.configSets.get(type).forEach(ConfigTracker::closeConfig);
    }

    static void openConfig(ModConfig config, Path configBasePath, @Nullable Path configOverrideBasePath) {
        LOGGER.trace(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
        if (config.loadedConfig != null) {
            LOGGER.warn("Opening a config that was already loaded with value {} at path {}", config.loadedConfig, config.getFileName());
        }
        var basePath = resolveBasePath(config, configBasePath, configOverrideBasePath);
        var configPath = basePath.resolve(config.getFileName());

        // Forge Config Api Port: invoke Fabric style callback instead of Forge event
        loadConfig(config, configPath, ModConfigEventsHelper.loading());
        LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath);

        // Forge Config Api Port: switch out config access
        if (!ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("disableConfigWatcher")) {
            FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(config, configPath, Thread.currentThread().getContextClassLoader()));
            LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath);
        }
    }

    private static Path resolveBasePath(ModConfig config, Path configBasePath, @Nullable Path configOverrideBasePath) {
        if (configOverrideBasePath != null) {
            Path overrideFilePath = configOverrideBasePath.resolve(config.getFileName());
            if (Files.exists(overrideFilePath)) {
                LOGGER.info(CONFIG, "Found config file override in path {}", overrideFilePath);
                return configOverrideBasePath;
            }
        }
        return configBasePath;
    }

    // Forge Config Api Port: adapt event constructor for Fabric style callback instead of Forge event
    static void loadConfig(ModConfig modConfig, Path path, Consumer<ModConfig> eventConstructor) {
        CommentedConfig config;

        try {
            // Load existing config
            config = readConfig(path);

            if (!modConfig.getSpec().isCorrect(config)) {
                LOGGER.warn(CONFIG, "Configuration file {} is not correct. Correcting", path);
                backUpConfig(path);
                modConfig.getSpec().correct(config);
                writeConfig(path, config);
            }
        } catch (NoSuchFileException ignored) {
            // Config file does not exist yet
            try {
                setupConfigFile(modConfig, path);
                config = readConfig(path);
            } catch (IOException | ParsingException ex) {
                throw new RuntimeException("Failed to create default config file " + modConfig.getFileName() + " of type " + modConfig.getType() + " for modid " + modConfig.getModId(), ex);
            }
        } catch (IOException | ParsingException ex) {
            // Failed to read existing file
            LOGGER.warn(CONFIG, "Failed to load config {}: {}. Attempting to recreate", modConfig.getFileName(), ex);
            try {
                backUpConfig(path);
                Files.delete(path);

                setupConfigFile(modConfig, path);
                config = readConfig(path);
            } catch (Throwable t) {
                ex.addSuppressed(t);

                throw new RuntimeException("Failed to recreate config file " + modConfig.getFileName() + " of type " + modConfig.getType() + " for modid " + modConfig.getModId(), ex);
            }
        }

        modConfig.setConfig(new LoadedConfig(config, path, modConfig), eventConstructor);
    }

    public static void acceptSyncedConfig(ModConfig modConfig, byte[] bytes) {
        if (modConfig.loadedConfig != null) {
            LOGGER.warn("Overwriting non-null config {} at path {} with synced config", modConfig.loadedConfig, modConfig.getFileName());
        }
        var newConfig = new SynchronizedConfig(InMemoryCommentedFormat.defaultInstance(), LinkedHashMap::new);
        newConfig.bulkCommentedUpdate(view -> {
            TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes), view, ParsingMode.REPLACE);
        });
        // TODO: do we want to do any validation? (what do we do if acceptConfig fails?)
        // Forge Config Api Port: invoke Fabric style callback instead of Forge event
        modConfig.setConfig(new LoadedConfig(newConfig, null, modConfig), ModConfigEventsHelper.reloading()); // TODO: should maybe be Loading on the first load?
    }

    public void loadDefaultServerConfigs() {
        configSets.get(ModConfig.Type.SERVER).forEach(modConfig -> {
            if (modConfig.loadedConfig != null) {
                LOGGER.warn("Overwriting non-null config {} at path {} with default server config", modConfig.loadedConfig, modConfig.getFileName());
            }

            // Forge Config Api Port: invoke Fabric style callback instead of Forge event
            modConfig.setConfig(new LoadedConfig(createDefaultConfig(modConfig.getSpec()), null, modConfig), ModConfigEventsHelper.loading());
        });
    }

    private static CommentedConfig createDefaultConfig(IConfigSpec spec) {
        var commentedConfig = new SynchronizedConfig(InMemoryCommentedFormat.defaultInstance(), LinkedHashMap::new);
        commentedConfig.bulkCommentedUpdate(spec::correct);
        return commentedConfig;
    }

    private static void closeConfig(ModConfig config) {
        if (config.loadedConfig != null) {
            if (config.loadedConfig.path() != null) {
                LOGGER.trace(CONFIG, "Closing config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
                unload(config.loadedConfig.path());
                // Forge Config Api Port: invoke Fabric style callback instead of Forge event
                config.setConfig(null, ModConfigEventsHelper.unloading());
            } else {
                LOGGER.warn(CONFIG, "Closing non-file config {} at path {}", config.loadedConfig, config.getFileName());
            }
        }
    }

    private static void unload(Path path) {
        // Forge Config Api Port: switch out config access
        if (ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("disableConfigWatcher"))
            return;
        try {
            FileWatcher.defaultInstance().removeWatch(path);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to remove config {} from tracker!", path, e);
        }
    }

    private static void setupConfigFile(ModConfig modConfig, Path file) throws IOException {
        Files.createDirectories(file.getParent());
        Path p = defaultConfigPath.resolve(modConfig.getFileName());
        if (Files.exists(p)) {
            LOGGER.info(CONFIG, "Loading default config file from path {}", p);
            Files.copy(p, file);
        } else {
            writeConfig(file, createDefaultConfig(modConfig.getSpec()));
        }
    }

    private static ConcurrentCommentedConfig readConfig(Path path) throws IOException, ParsingException {
        try (var reader = Files.newBufferedReader(path)) {
            var config = new SynchronizedConfig(TomlFormat.instance(), LinkedHashMap::new);
            config.bulkCommentedUpdate(view -> {
                new TomlParser().parse(reader, view, ParsingMode.REPLACE);
            });
            return config;
        }
    }

    static void writeConfig(Path file, UnmodifiableCommentedConfig config) {
        new TomlWriter().write(config, file, WritingMode.REPLACE_ATOMIC);
    }

    private static void backUpConfig(Path commentedFileConfig) {
        backUpConfig(commentedFileConfig, 5); //TODO: Think of a way for mods to set their own preference (include a sanity check as well, no disk stuffing)
    }

    private static void backUpConfig(Path commentedFileConfig, int maxBackups) {
        Path bakFileLocation = commentedFileConfig.getParent();
        String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFileName().toString());
        String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFileName().toString()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try {
            for (int i = maxBackups; i > 0; i--) {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if (Files.exists(oldBak)) {
                    if (i >= maxBackups)
                        Files.delete(oldBak);
                    else
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                }
            }
            Files.copy(commentedFileConfig, bakFile);
        } catch (IOException exception) {
            LOGGER.warn(CONFIG, "Failed to back up config file {}", commentedFileConfig, exception);
        }
    }
}
