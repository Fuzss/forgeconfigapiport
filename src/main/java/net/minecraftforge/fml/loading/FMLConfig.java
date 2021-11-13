package net.minecraftforge.fml.loading;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.ForgeConfigAPIPort.CORE;

public class FMLConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_CONFIG_NAME = "defaultconfigs";

    public static void loadDefaultConfigPath() {
        LOGGER.trace(CORE, "Default config paths at {}", DEFAULT_CONFIG_NAME);
        FileUtils.getOrCreateDirectory(FabricLoader.getInstance().getGameDir().resolve(DEFAULT_CONFIG_NAME), "default config directory");
    }
}
