/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ForgeConfigApiPortConfig {
    private static final ConfigSpec configSpec = new ConfigSpec();
    private static final CommentedConfig configComments = CommentedConfig.inMemory();
    private static final ForgeConfigApiPortConfig INSTANCE;

    static {
        for (ModConfigValues cv : ModConfigValues.values()) {
            cv.buildConfigEntry(configSpec, configComments);
        }
        INSTANCE = new ForgeConfigApiPortConfig();
    }

    @Nullable
    private CommentedFileConfig configData;

    private ForgeConfigApiPortConfig() {
        // NO-OP
    }

    private void loadFrom(Path configFile) {
        this.configData = CommentedFileConfig.builder(configFile)
                .sync()
                .onFileNotFound(FileNotFoundAction.copyData(Objects.requireNonNull(this.getClass()
                        .getResourceAsStream("/" + ForgeConfigAPIPort.MOD_ID + ".toml"))))
                .writingMode(WritingMode.REPLACE)
                .build();
        try {
            this.configData.load();
        } catch (ParsingException e) {
            throw new RuntimeException("Failed to load FML config from " + configFile, e);
        }

        if (!configSpec.isCorrect(this.configData)) {
            ForgeConfigAPIPort.LOGGER.warn("Configuration file {} is not correct. Correcting", configFile);
            configSpec.correct(this.configData,
                    (action, path, incorrectValue, correctedValue) -> ForgeConfigAPIPort.LOGGER.info(
                            "Incorrect key {} was corrected from {} to {}",
                            path,
                            incorrectValue,
                            correctedValue));
        }

        this.configData.putAllComments(configComments);
        this.configData.save();
    }

    public static void load() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve(ForgeConfigAPIPort.MOD_ID + ".toml");
        INSTANCE.loadFrom(configFile);
        ForgeConfigAPIPort.LOGGER.trace("Loaded {} config from {}", ForgeConfigAPIPort.MOD_NAME, configFile);
        for (ModConfigValues modConfigValues : ModConfigValues.values()) {
            ForgeConfigAPIPort.LOGGER.trace("{} {} is {}",
                    ForgeConfigAPIPort.MOD_NAME,
                    modConfigValues.entry,
                    modConfigValues.getConfigValue(INSTANCE.configData));
        }

        getOrCreateGameRelativePath(Paths.get(getConfigValue(ModConfigValues.DEFAULT_CONFIGS_PATH)));
    }

    public static String getConfigValue(ModConfigValues modConfigValues) {
        if (INSTANCE.configData == null) {
            load();
        }

        return modConfigValues.getConfigValue(INSTANCE.configData);
    }

    public static boolean getBoolConfigValue(ModConfigValues modConfigValues) {
        if (INSTANCE.configData == null) {
            load();
        }

        return modConfigValues.getConfigValue(INSTANCE.configData);
    }

    public static Path getDefaultConfigsDirectory() {
        return FabricLoader.getInstance().getGameDir().resolve(getConfigValue(ModConfigValues.DEFAULT_CONFIGS_PATH));
    }

    /**
     * Copied from {@code net.minecraftforge.fml.loading.FMLPaths}.
     */
    private static Path getOrCreateGameRelativePath(Path path) {
        Path gameFolderPath = FabricLoader.getInstance().getGameDir().resolve(path);
        if (!Files.isDirectory(gameFolderPath)) {
            try {
                Files.createDirectories(gameFolderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return gameFolderPath;
    }
}
