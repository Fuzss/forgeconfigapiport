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

    private void loadFrom(final Path configFile) {
        this.configData = CommentedFileConfig.builder(configFile)
                .sync()
                .onFileNotFound(FileNotFoundAction.copyData(Objects.requireNonNull(
                        getClass().getResourceAsStream("/" + ForgeConfigAPIPort.MOD_ID + ".toml"))))
                .writingMode(WritingMode.REPLACE)
                .build();
        try {
            this.configData.load();
        } catch (ParsingException exception) {
            try {
                Files.delete(this.configData.getNioPath());
                this.configData.load();
                ForgeConfigAPIPort.LOGGER.warn("Configuration file {} could not be parsed. Correcting",
                        this.configData.getNioPath(),
                        exception);
            } catch (ParsingException ignored) {
                // don't let this fail just because some random rarely used config cannot be properly loaded
            } catch (Throwable throwable) {
                throw new RuntimeException(
                        "Failed to load " + ForgeConfigAPIPort.MOD_NAME + " config from " + configFile, throwable);
            }
        }
        if (!configSpec.isCorrect(this.configData)) {
            ForgeConfigAPIPort.LOGGER.warn("Configuration file {} is not correct. Correcting", configFile);
            configSpec.correct(this.configData,
                    (action, path, incorrectValue, correctedValue) -> ForgeConfigAPIPort.LOGGER.info(
                            "Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue)
            );
        }
        this.configData.putAllComments(configComments);
        this.configData.save();
    }

    public static void load() {
        final Path configFile = FabricLoader.getInstance().getConfigDir().resolve(ForgeConfigAPIPort.MOD_ID + ".toml");
        INSTANCE.loadFrom(configFile);
        ForgeConfigAPIPort.LOGGER.trace("Loaded FML config from {}", configFile);
        for (ModConfigValues cv : ModConfigValues.values()) {
            ForgeConfigAPIPort.LOGGER.trace("FMLConfig {} is {}", cv.entry, cv.getConfigValue(INSTANCE.configData));
        }
        getOrCreateGameRelativePath(Paths.get(getConfigValue(ModConfigValues.DEFAULT_CONFIGS_PATH)));
    }

    public static String getConfigValue(ModConfigValues v) {
        if (INSTANCE.configData == null) load();
        return v.getConfigValue(INSTANCE.configData);
    }

    public static boolean getBoolConfigValue(ModConfigValues v) {
        if (INSTANCE.configData == null) load();
        return v.getConfigValue(INSTANCE.configData);
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
