package fuzs.forgeconfigapiport.impl.util;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.impl.core.CommonAbstractions;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class ConfigLoadingUtil {
    public static final Map<String, Map<String, Object>> DEFAULT_CONFIG_VALUES = Maps.newConcurrentMap();

    public static void tryLoadConfigFile(FileConfig configData) {
        // Forge Config Api Port: common issue during config loading (from file) is com.electronwill.nightconfig.core.io.ParsingException: Not enough data available
        // this is usually caused by a malformed or corrupted file, so we delete the file and try to load again (which will execute the FileNotFoundAction which generates a new file from scratch)
        tryLoadConfigFile(configData, () -> ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("recreateConfigsWhenParsingFails"));
    }

    private static void tryLoadConfigFile(FileConfig configData, BooleanSupplier recreate) {
        try {
            configData.load();
        } catch (ParsingException e) {
            if (recreate.getAsBoolean()) {
                try {
                    backUpConfig(configData.getNioPath(), 5);
                    Files.delete(configData.getNioPath());
                    configData.load();
                    ForgeConfigAPIPort.LOGGER.warn("Configuration file {} could not be parsed. Correcting", configData.getNioPath());
                    return;
                } catch (Throwable t) {
                    e.addSuppressed(t);
                }
            }
            throw e;
        }
    }

    public static void tryRegisterDefaultConfig(ModConfig modConfig) {
        if (!ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("correctConfigValuesFromDefaultConfig")) return;
        String fileName = modConfig.getFileName();
        Path path = CommonAbstractions.INSTANCE.getDefaultConfigsDirectory().resolve(fileName);
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

    public static void backUpConfig(final Path commentedFileConfig, final int maxBackups) {
        if (!Files.exists(commentedFileConfig)) return;
        Path bakFileLocation = commentedFileConfig.getParent();
        String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFileName().toString());
        String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFileName().toString()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try {
            for (int i = maxBackups; i > 0; i--) {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if (Files.exists(oldBak)) {
                    if (i >= maxBackups) Files.delete(oldBak);
                    else
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                }
            }
            Files.copy(commentedFileConfig, bakFile);
        } catch (IOException exception) {
            ForgeConfigAPIPort.LOGGER.warn("Failed to back up config file {}", commentedFileConfig, exception);
        }
    }
}
