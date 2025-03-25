package fuzs.forgeconfigapiport.forge.impl.neoforge;

import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * TODO remove ModConfig parameters, change IConfigSpec parameter to ModConfigSpec
 */
public final class NeoForgeConfigRegistryImpl implements NeoForgeConfigRegistry {

    @Override
    public ModConfig register(ModConfig.Type type, IConfigSpec spec) {
        return this.register(ModLoadingContext.get().getActiveNamespace(), type, spec);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec) {
        return this.register(getModContainer(modId), type, spec);
    }

    @Override
    public ModConfig register(ModContainer modContainer, ModConfig.Type type, IConfigSpec spec) {
        // use the internal class to be able to return the ModConfig instance, remove the return value in the future
        ModConfig modConfig = new ModConfig(type, new NeoForgeConfigSpecAdapter(modContainer.getModId(),
                (ModConfigSpec) spec
        ), modContainer);
        modContainer.addConfig(modConfig);
        return modConfig;
    }

    @Override
    public ModConfig register(ModConfig.Type type, IConfigSpec spec, String fileName) {
        return this.register(ModLoadingContext.get().getActiveNamespace(), type, spec, fileName);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec spec, String fileName) {
        return this.register(getModContainer(modId), type, spec, fileName);
    }

    @Override
    public ModConfig register(ModContainer modContainer, ModConfig.Type type, IConfigSpec spec, String fileName) {
        // use the internal class to be able to return the ModConfig instance, remove the return value in the future
        ModConfig modConfig = new ModConfig(type, new NeoForgeConfigSpecAdapter(modContainer.getModId(),
                (ModConfigSpec) spec
        ), modContainer, fileName);
        modContainer.addConfig(modConfig);
        return modConfig;
    }

    static ModContainer getModContainer(String modId) {
        return ModList.get()
                .getModContainerById(modId)
                .orElseThrow(() -> new IllegalStateException("Invalid mod id '%s'".formatted(modId)));
    }
}
