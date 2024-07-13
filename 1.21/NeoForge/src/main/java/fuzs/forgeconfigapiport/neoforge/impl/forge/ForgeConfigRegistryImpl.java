package fuzs.forgeconfigapiport.neoforge.impl.forge;

import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.IConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(ModConfig.Type type, IConfigSpec<?> spec) {
        return this.register(ModLoadingContext.get().getActiveNamespace(), type, spec);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        return this.register(getModContainer(modId), type, spec);
    }

    @Override
    public ModConfig register(ModContainer modContainer, ModConfig.Type type, IConfigSpec<?> spec) {
        // TODO use the internal class to be able to return the ModConfig instance, remove the return value in the future
        return ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter((ForgeConfigSpec) spec), modContainer);
    }

    @Override
    public ModConfig register(ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        return this.register(ModLoadingContext.get().getActiveNamespace(), type, spec, fileName);
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        return this.register(getModContainer(modId), type, spec, fileName);
    }

    @Override
    public ModConfig register(ModContainer modContainer, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        // TODO use the internal class to be able to return the ModConfig instance, remove the return value in the future
        return ConfigTracker.INSTANCE.registerConfig(type, new ForgeConfigSpecAdapter((ForgeConfigSpec) spec), modContainer, fileName);
    }

    static ModContainer getModContainer(String modId) {
        return ModList.get()
                .getModContainerById(modId)
                .orElseThrow(() -> new IllegalStateException("Invalid mod id '%s'".formatted(modId)));
    }
}
