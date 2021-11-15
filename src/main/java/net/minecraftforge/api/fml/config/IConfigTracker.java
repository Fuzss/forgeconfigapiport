package net.minecraftforge.api.fml.config;

import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * extracted two methods for getting config data which need to be frequently accessed
 */
public interface IConfigTracker {
    /**
     * Forge config tracker instance
     */
    IConfigTracker INSTANCE = ConfigTracker.INSTANCE;

    /**
     * @return mod configs sorted by type
     */
    Map<ModConfig.Type, Set<ModConfig>> configSets();

    /**
     * @return all mod configs mapped to mod config file name
     */
    ConcurrentHashMap<String, ModConfig> fileMap();
}
