package fuzs.forgeconfigapiport.neoforge.impl.forge;

import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForwardingConfigSpec;
import net.minecraftforge.fml.config.IConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        ModContainer modContainer = ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalStateException("invalid mod id '%s'".formatted(modId)));
        ModConfig modConfig = new ModConfig(type, new ForwardingConfigSpec<>(spec), modContainer);
        modContainer.addConfig(modConfig);
        return modConfig;
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        ModContainer modContainer = ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalStateException("invalid mod id '%s'".formatted(modId)));
        ModConfig modConfig = new ModConfig(type, new ForwardingConfigSpec<>(spec), modContainer, fileName);
        modContainer.addConfig(modConfig);
        return modConfig;
    }
}
