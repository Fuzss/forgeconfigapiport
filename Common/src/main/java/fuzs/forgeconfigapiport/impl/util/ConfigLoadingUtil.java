package fuzs.forgeconfigapiport.impl.util;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigApiPortConfig;

import java.nio.file.Files;

public class ConfigLoadingUtil {

    public static void tryLoadConfigFile(FileConfig configData) {
        try {
            configData.load();
        } catch (ParsingException e) {
            // Forge Config Api Port: common issue during config loading (from file) is com.electronwill.nightconfig.core.io.ParsingException: Not enough data available
            // this is usually caused by a malformed or corrupted file, so we delete the file and try to load again (which will execute the FileNotFoundAction which generates a new file from scratch)
            if (ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("recreateConfigsWhenParsingFails")) {
                try {
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
}
