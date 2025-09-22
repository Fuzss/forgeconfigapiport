package fuzs.forgeconfigapiport.fabric.impl.core;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

public final class ConfigRegistryImpl implements ConfigRegistry {

    @Override
    public void register(String modId, ModConfig.Type type, IConfigSpec spec) {
        if (spec.isEmpty()) {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            ForgeConfigAPIPort.LOGGER.debug("Attempted to register an empty config for type {} on mod {}", type, modId);
        } else {
            ConfigTracker.INSTANCE.registerConfig(type, spec, modId);
        }
    }

    @Override
    public void register(String modId, ModConfig.Type type, IConfigSpec spec, String fileName) {
        if (spec.isEmpty()) {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            ForgeConfigAPIPort.LOGGER.debug(
                    "Attempted to register an empty config for type {} on mod {} using file name {}",
                    type,
                    modId,
                    fileName);
        } else {
            ConfigTracker.INSTANCE.registerConfig(type, spec, modId, fileName);
        }
    }

    @Override
    public void register(String modId, net.neoforged.fml.config.ModConfig.Type type, net.minecraftforge.fml.config.IConfigSpec<?> spec) {
        if (spec.isEmpty()) {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            ForgeConfigAPIPort.LOGGER.debug("Attempted to register an empty config for type {} on mod {}", type, modId);
        } else {
            ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter(spec), modId);
        }
    }

    @Override
    public void register(String modId, net.neoforged.fml.config.ModConfig.Type type, net.minecraftforge.fml.config.IConfigSpec<?> spec, String fileName) {
        if (spec.isEmpty()) {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            ForgeConfigAPIPort.LOGGER.debug(
                    "Attempted to register an empty config for type {} on mod {} using file name {}",
                    type,
                    modId,
                    fileName);
        } else {
            ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter(spec), modId, fileName);
        }
    }
}
