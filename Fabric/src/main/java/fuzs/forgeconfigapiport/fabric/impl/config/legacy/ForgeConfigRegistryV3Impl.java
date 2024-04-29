package fuzs.forgeconfigapiport.fabric.impl.config.legacy;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigRegistry;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

public final class ForgeConfigRegistryV3Impl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        return new ModConfig(type, spec, modId);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        return new ModConfig(type, spec, modId, fileName);
    }
}
