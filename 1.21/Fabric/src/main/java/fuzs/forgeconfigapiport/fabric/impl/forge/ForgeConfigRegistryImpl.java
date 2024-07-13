package fuzs.forgeconfigapiport.fabric.impl.forge;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.neoforged.fml.config.ConfigTracker;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        this.register(modId, unwrap(type), (ForgeConfigSpec) spec);
        return null;
    }

    @Override
    public void register(String modId, net.neoforged.fml.config.ModConfig.Type type, IConfigSpec<?> spec) {
        ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter(spec), modId);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        this.register(modId, unwrap(type), (ForgeConfigSpec) spec, fileName);
        return null;
    }

    @Override
    public void register(String modId, net.neoforged.fml.config.ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter(spec), modId, fileName);
    }

    static net.neoforged.fml.config.ModConfig.Type unwrap(ModConfig.Type type) {
        return net.neoforged.fml.config.ModConfig.Type.valueOf(type.name());
    }
}
