package fuzs.forgeconfigapiport.impl.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Objects;

public class ForgeConfigApiPortConfig {
    public static final ForgeConfigApiPortConfig INSTANCE;
    private static final String CONFIG_FILE_NAME = ForgeConfigAPIPort.MOD_ID + ".toml";
    private static final ConfigSpec CONFIG_SPEC;

    static {
        CONFIG_SPEC = new ConfigSpec();
        CONFIG_SPEC.define("defaultConfigsPath", "defaultconfigs");
        CONFIG_SPEC.define("forceGlobalServerConfigs", true);
        CONFIG_SPEC.define("disableConfigCommand", false);
        CONFIG_SPEC.define("recreateConfigsWhenParsingFails", true);
        INSTANCE = new ForgeConfigApiPortConfig();
    }

    private CommentedFileConfig configData;

    private ForgeConfigApiPortConfig() {
        this.loadFrom(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME));
    }

    // copied from FML config
    private void loadFrom(final Path configFile) {
        this.configData = CommentedFileConfig.builder(configFile).sync().onFileNotFound(FileNotFoundAction.copyData(Objects.requireNonNull(this.getClass().getResourceAsStream("/" + CONFIG_FILE_NAME)))).autosave().autoreload().writingMode(WritingMode.REPLACE).build();
        try {
            this.configData.load();
        } catch (ParsingException e) {
            throw new RuntimeException("Failed to load %s config from %s".formatted(ForgeConfigAPIPort.MOD_NAME, configFile), e);
        }
        if (!CONFIG_SPEC.isCorrect(this.configData)) {
            ForgeConfigAPIPort.LOGGER.warn("Configuration file {} is not correct. Correcting", configFile);
            CONFIG_SPEC.correct(this.configData, (action, path, incorrectValue, correctedValue) -> ForgeConfigAPIPort.LOGGER.warn("Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue));
        }
        this.configData.save();
    }

    public <T> T getValue(String path, T defaultValue) {
        return this.configData.<T>getOptional(path).orElse(defaultValue);
    }
}
