package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.fml.config.IConfigTracker;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigTracker implements IConfigTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker CONFIG = MarkerManager.getMarker("CONFIG");
    public static final ConfigTracker INSTANCE = new ConfigTracker();
    private final ConcurrentHashMap<String, ModConfig> fileMap;
    private final EnumMap<ModConfig.Type, Set<ModConfig>> configSets;
    private final ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>> configsByMod;

    private ConfigTracker() {
        this.fileMap = new ConcurrentHashMap<>();
        this.configSets = new EnumMap<>(ModConfig.Type.class);
        this.configsByMod = new ConcurrentHashMap<>();
        this.configSets.put(ModConfig.Type.CLIENT, Collections.synchronizedSet(new LinkedHashSet<>()));
        this.configSets.put(ModConfig.Type.COMMON, Collections.synchronizedSet(new LinkedHashSet<>()));
//        this.configSets.put(ModConfig.Type.PLAYER, new ConcurrentSkipListSet<>());
        this.configSets.put(ModConfig.Type.SERVER, Collections.synchronizedSet(new LinkedHashSet<>()));
    }

    void trackConfig(final ModConfig config) {
        if (this.fileMap.containsKey(config.getFileName())) {
            LOGGER.error(CONFIG,"Detected config file conflict {} between {} and {}", config.getFileName(), this.fileMap.get(config.getFileName()).getModId(), config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }
        this.fileMap.put(config.getFileName(), config);
        this.configSets.get(config.getType()).add(config);
        this.configsByMod.computeIfAbsent(config.getModId(), (k)->new EnumMap<>(ModConfig.Type.class)).put(config.getType(), config);
        LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
        loadConfig(config, FabricLoader.getInstance().getConfigDir());
    }

    private void loadConfig(ModConfig config, Path configBasePath) {
        // unlike on forge there isn't really more than one loading stage for mods on fabric, therefore we load configs immediately
        if (config.getType() != ModConfig.Type.SERVER) {
            openConfig(config, configBasePath);
        }
    }

    public void loadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Loading configs type {}", type);
        this.configSets.get(type).forEach(config -> openConfig(config, configBasePath));
    }

    public void unloadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Unloading configs type {}", type);
        this.configSets.get(type).forEach(config -> closeConfig(config, configBasePath));
    }

    private void openConfig(final ModConfig config, final Path configBasePath) {
        LOGGER.trace(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
        final CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        ModConfigEvent.LOADING.invoker().onModConfigLoading(config);
        config.save();
    }

    private void closeConfig(final ModConfig config, final Path configBasePath) {
        if (config.getConfigData() != null) {
            LOGGER.trace(CONFIG, "Closing config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
            config.save();
            config.getHandler().unload(configBasePath, config);
            config.setConfigData(null);
        }
    }

    public void loadDefaultServerConfigs() {
        configSets.get(ModConfig.Type.SERVER).forEach(modConfig -> {
            final CommentedConfig commentedConfig = CommentedConfig.inMemory();
            modConfig.getSpec().correct(commentedConfig);
            modConfig.setConfigData(commentedConfig);
            ModConfigEvent.LOADING.invoker().onModConfigLoading(modConfig);
        });
    }

    public String getConfigFileName(String modId, ModConfig.Type type) {
        return Optional.ofNullable(configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null)).
                map(ModConfig::getFullPath).map(Object::toString).orElse(null);
    }

    @Override
    public Map<ModConfig.Type, Set<ModConfig>> configSets() {
        return configSets;
    }

    @Override
    public ConcurrentHashMap<String, ModConfig> fileMap() {
        return fileMap;
    }
}
