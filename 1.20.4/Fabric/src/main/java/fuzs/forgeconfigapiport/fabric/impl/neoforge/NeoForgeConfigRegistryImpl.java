package fuzs.forgeconfigapiport.fabric.impl.neoforge;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

public final class NeoForgeConfigRegistryImpl implements NeoForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        return new ModConfig(type, spec, modId);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        return new ModConfig(type, spec, modId, fileName);
    }
}
