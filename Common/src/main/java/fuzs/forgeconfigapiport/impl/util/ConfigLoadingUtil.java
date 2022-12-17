package fuzs.forgeconfigapiport.impl.util;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.core.CommonAbstractions;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;

import java.io.IOException;
import java.nio.file.Files;

public class ConfigLoadingUtil {

    public static void tryLoadConfigFile(ModConfig modConfig, FileConfig fileConfig) {
        try {
            fileConfig.load();
        } catch (ParsingException ex) {
            // Forge Config Api Port: common issue during config loading (from file) is com.electronwill.nightconfig.core.io.ParsingException: Not enough data available
            // this is usually caused by a malformed or corrupted file, so we delete the file and try to load again (which will execute the FileNotFoundAction which generates a new file from scratch)
            if (CommonAbstractions.INSTANCE.recreateConfigsWhenParsingFails()) {
                ForgeConfigAPIPort.LOGGER.warn("Configuration file {} is not correct. Correcting", fileConfig.getNioPath());
                try {
                    Files.delete(fileConfig.getNioPath());
                    fileConfig.load();
                } catch (IOException | ParsingException ex1) {
                    throw new ConfigFileTypeHandler.ConfigLoadingException(modConfig, ex);
                }
            } else {
                throw new ConfigFileTypeHandler.ConfigLoadingException(modConfig, ex);
            }
        }
    }
}
