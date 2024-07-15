package fuzs.forgeconfigapiport.fabric.impl.core;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.neoforged.fml.config.ConfigTracker;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        ModConfig modConfig = ConfigTracker.INSTANCE.registerConfig(unwrap(type), new ForgeConfigSpecAdapter(spec),
                modId
        ).modConfig;
        Objects.requireNonNull(modConfig, "mod config is null");
        return modConfig;
    }

    @Override
    public void register(String modId, net.neoforged.fml.config.ModConfig.Type type, IConfigSpec<?> spec) {
        ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter(spec), modId);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        ModConfig modConfig = ConfigTracker.INSTANCE.registerConfig(unwrap(type), new ForgeConfigSpecAdapter(spec),
                modId, fileName
        ).modConfig;
        Objects.requireNonNull(modConfig, "mod config is null");
        return modConfig;
    }

    @Override
    public void register(String modId, net.neoforged.fml.config.ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter(spec), modId, fileName);
    }

    static net.neoforged.fml.config.ModConfig.Type unwrap(ModConfig.Type type) {
        return net.neoforged.fml.config.ModConfig.Type.valueOf(type.name());
    }

    static ModConfig.Type wrap(net.neoforged.fml.config.ModConfig.Type type) {
        return ModConfig.Type.valueOf(type.name());
    }

    @Nullable
    public static ModConfig adapt(net.neoforged.fml.config.ModConfig modConfig) {
        if (modConfig.getSpec() instanceof ForgeConfigSpecAdapter adapter) {
            return new ModConfig(wrap(modConfig.getType()), adapter.spec(), modConfig.getModId(),
                    modConfig.getFileName()
            );
        } else {
            return null;
        }
    }
}
