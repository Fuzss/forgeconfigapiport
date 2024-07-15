package fuzs.forgeconfigapiport.fabric.impl.core;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

public final class NeoForgeConfigRegistryImpl implements NeoForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec) {
        return ConfigTracker.INSTANCE.registerConfig(type, spec, modId);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec, String fileName) {
        return ConfigTracker.INSTANCE.registerConfig(type, spec, modId, fileName);
    }
}
