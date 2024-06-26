/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.fml.config;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import fuzs.forgeconfigapiport.fabric.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.fabric.impl.util.ConfigLoadingHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static net.neoforged.fml.config.ConfigTracker.CONFIG;

public class ConfigFileTypeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    public final static ConfigFileTypeHandler TOML = new ConfigFileTypeHandler();
    // Forge Config Api Port: adapted for Fabric
    private static final Path defaultConfigPath = ForgeConfigApiPortConfig.getDefaultConfigsDirectory();

    public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return (c) -> {
            final Path configPath = configBasePath.resolve(c.getFileName());
            // Forge Config API Port:
            // force toml format which is normally auto-detected by night config from the file extension
            // there have been reports of this failing and the auto-detection not working
            // my guess is this is caused by the toml format not having been registered as the registration is done in a static initializer in the class itself
            // due to the clazz not having been initialized by the classloader (see java language specification 12.2)
            // forcing the format or calling any static method on the class will guarantee it to be initialized
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath, TomlFormat.instance())
                    .sync()
                    .preserveInsertionOrder()
                    .autosave()
                    .onFileNotFound((newfile, configFormat) -> this.setupConfigFile(c, newfile, configFormat))
                    .writingMode(WritingMode.REPLACE)
                    .build();
            LOGGER.debug(ConfigTracker.CONFIG, "Built TOML config for {}", configPath);
            try {
                configData.load();
            } catch (ParsingException ex) {
                LOGGER.warn(CONFIG, "Attempting to recreate {}", configPath);
                try {
                    backUpConfig(configData.getNioPath(), 5);
                    Files.delete(configData.getNioPath());

                    configData.load();
                } catch (Throwable t) {
                    ex.addSuppressed(t);

                    throw new ConfigLoadingException(c, ex);
                }
            }
            LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath);
            // Forge Config API Port: store values from default config, so we can retrieve them when correcting individual values
            ConfigLoadingHelper.tryRegisterDefaultConfig(c.getFileName());
            if (!ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("disableConfigWatcher")) {
                try {
                    FileWatcher.defaultInstance()
                            .addWatch(configPath,
                                    new ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader())
                            );
                    LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath);
                } catch (IOException e) {
                    throw new RuntimeException("Couldn't watch config file", e);
                }
            }
            return configData;
        };
    }

    public void unload(ModConfig config) {
        if (ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("disableConfigWatcher")) {
            return;
        }
        Path configPath = config.getFullPath();
        try {
            FileWatcher.defaultInstance().removeWatch(configPath);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to remove config {} from tracker!", configPath, e);
        }
    }

    private boolean setupConfigFile(final ModConfig modConfig, final Path file, final ConfigFormat<?> conf) throws IOException {
        Files.createDirectories(file.getParent());
        Path p = defaultConfigPath.resolve(modConfig.getFileName());
        if (Files.exists(p)) {
            LOGGER.info(CONFIG, "Loading default config file from path {}", p);
            Files.copy(p, file);
        } else {
            Files.createFile(file);
            conf.initEmptyFile(file);
        }
        return true;
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig) {
        backUpConfig(commentedFileConfig, 5); //TODO: Think of a way for mods to set their own preference (include a sanity check as well, no disk stuffing)
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig, final int maxBackups) {
        backUpConfig(commentedFileConfig.getNioPath(), maxBackups);
    }

    public static void backUpConfig(final Path commentedFileConfig, final int maxBackups) {
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

    private static class ConfigWatcher implements Runnable {
        private final ModConfig modConfig;
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;

        ConfigWatcher(final ModConfig modConfig, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.modConfig = modConfig;
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }

        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(this.realClassLoader);
            if (!this.modConfig.getSpec().isCorrecting()) {
                try {
                    this.commentedFileConfig.load();
                    if (!this.modConfig.getSpec().isCorrect(this.commentedFileConfig)) {
                        LOGGER.warn(CONFIG,
                                "Configuration file {} is not correct. Correcting",
                                this.commentedFileConfig.getFile().getAbsolutePath()
                        );
                        ConfigFileTypeHandler.backUpConfig(this.commentedFileConfig);
                        this.modConfig.getSpec().correct(this.commentedFileConfig);
                        this.commentedFileConfig.save();
                    }
                } catch (ParsingException ex) {
                    throw new ConfigLoadingException(this.modConfig, ex);
                }
                LOGGER.debug(CONFIG, "Config file {} changed, sending notifies", this.modConfig.getFileName());
                this.modConfig.getSpec().afterReload();
                // Forge Config API Port: invoke Fabric style callback instead of Forge event
                NeoForgeModConfigEvents.reloading(this.modConfig.getModId())
                        .invoker()
                        .onModConfigReloading(this.modConfig);
            }
        }
    }

    private static class ConfigLoadingException extends RuntimeException {
        public ConfigLoadingException(ModConfig config, Exception cause) {
            super("Failed loading config file " + config.getFileName() + " of type " + config.getType() +
                    " for modid " + config.getModId(), cause);
        }
    }
}