package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        verifyModId(modId);
        return new ModConfig(type, spec, modId);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        verifyModId(modId);
        return new ModConfig(type, spec, modId, fileName);
    }

    private static void verifyModId(String modId) {
        FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalArgumentException("No mod with mod id %s".formatted(modId)));
    }
}
