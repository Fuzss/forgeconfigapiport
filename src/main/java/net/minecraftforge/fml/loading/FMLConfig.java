package net.minecraftforge.fml.loading;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.ConfigPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.ForgeConfigAPIPort.CORE;

public class FMLConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DEFAULT_CONFIG_PATH_NAME = ConfigPaths.DEFAULT_CONFIGS_PATH;

    public static void loadDefaultConfigPath() {
        LOGGER.trace(CORE, "Default config paths at {}", DEFAULT_CONFIG_PATH_NAME);
        FileUtils.getOrCreateDirectory(FabricLoader.getInstance().getGameDir().resolve(DEFAULT_CONFIG_PATH_NAME), "default config directory");
    }
}
