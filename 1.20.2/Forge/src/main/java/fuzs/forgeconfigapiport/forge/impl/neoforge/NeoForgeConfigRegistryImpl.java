package fuzs.forgeconfigapiport.forge.impl.neoforge;

import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigSpecAdapter;
import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.neoforged.fml.config.IConfigSpec;

public final class NeoForgeConfigRegistryImpl implements NeoForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        ModContainer modContainer = ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalStateException("invalid mod id '%s'".formatted(modId)));
        ModConfig modConfig = new ModConfig(type, new NeoForgeConfigSpecAdapter(spec), modContainer);
        modContainer.addConfig(modConfig);
        return modConfig;
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        ModContainer modContainer = ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalStateException("invalid mod id '%s'".formatted(modId)));
        ModConfig modConfig = new ModConfig(type, new NeoForgeConfigSpecAdapter(spec), modContainer, fileName);
        modContainer.addConfig(modConfig);
        return modConfig;
    }
}
