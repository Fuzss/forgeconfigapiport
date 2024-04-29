package fuzs.forgeconfigapiport.fabric.impl.forge;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        return new ModConfig(type, spec, modId);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        return new ModConfig(type, spec, modId, fileName);
    }
}
