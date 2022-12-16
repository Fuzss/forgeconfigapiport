package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        return new ModConfig(type, spec, this.getActiveModContainer(modId));
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        return new ModConfig(type, spec, this.getActiveModContainer(modId), fileName);
    }

    private ModContainer getActiveModContainer(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalArgumentException("No mod with mod id %s".formatted(modId)));
    }
}
