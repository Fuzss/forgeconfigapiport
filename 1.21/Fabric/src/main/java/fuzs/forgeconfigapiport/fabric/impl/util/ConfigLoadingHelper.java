package fuzs.forgeconfigapiport.fabric.impl.util;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.forgeconfigapiport.fabric.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.neoforged.fml.config.ConfigFileTypeHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigLoadingHelper {
    public static final Map<String, Map<String, Object>> DEFAULT_CONFIG_VALUES = Maps.newConcurrentMap();

    public static void tryLoadConfigFile(FileConfig configData) {
        // Forge Config Api Port: common issue during config loading (from file) is com.electronwill.nightconfig.core.io.ParsingException: Not enough data available
        // this is usually caused by a malformed or corrupted file, so we delete the file and try to load again (which will execute the FileNotFoundAction which generates a new file from scratch)
        try {
            configData.load();
        } catch (ParsingException exception) {
            try {
                ConfigFileTypeHandler.backUpConfig(configData.getNioPath(), 5);
                Files.delete(configData.getNioPath());
                configData.load();
                ForgeConfigAPIPort.LOGGER.warn("Configuration file {} could not be parsed. Correcting", configData.getNioPath());
                return;
            } catch (Throwable throwable) {
                exception.addSuppressed(throwable);
            }
            throw exception;
        }
    }

    public static void tryRegisterDefaultConfig(String fileName) {
        if (!ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("correctConfigValuesFromDefaultConfig")) return;
        Path path = ForgeConfigApiPortConfig.getDefaultConfigsDirectory().resolve(fileName);
        if (Files.exists(path)) {
            try (CommentedFileConfig config = CommentedFileConfig.of(path)) {
                config.load();
                // just get the values map, no need to hold on to the resource itself
                Map<String, Object> values = config.valueMap();
                if (values != null && !values.isEmpty()) {
                    DEFAULT_CONFIG_VALUES.put(fileName.intern(), ImmutableMap.copyOf(values));
                }
                ForgeConfigAPIPort.LOGGER.debug("Loaded default config values for future corrections from file at path {}", path);
            } catch (Exception ignored) {

            }
        }
    }
}
