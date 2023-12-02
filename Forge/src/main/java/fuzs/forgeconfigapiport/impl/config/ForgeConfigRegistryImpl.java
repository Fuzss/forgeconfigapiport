package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigRegistry;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        if (!modContainer.getNamespace().equals(modId)) {
            throw new IllegalStateException("active namespace '%s' does not match mod id '%s'".formatted(modContainer.getNamespace(), modId));
        } else {
            // FIXME for 1.21 when Forge classes have been removed from Forge Config Api Port, so the actual Forge constructor becomes accessible from here
            ModConfig modConfig = new ModConfig(toModConfigType(type), new ForwardingConfigSpec<>(spec), modContainer);
            modContainer.addConfig(modConfig);
            return modConfig;
        }
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        if (!modContainer.getNamespace().equals(modId)) {
            throw new IllegalStateException("active namespace '%s' does not match mod id '%s'".formatted(modContainer.getNamespace(), modId));
        } else {
            // FIXME for 1.21 when Forge classes have been removed from Forge Config Api Port, so the actual Forge constructor becomes accessible from here
            ModConfig modConfig = new ModConfig(toModConfigType(type), new ForwardingConfigSpec<>(spec), modContainer, fileName);
            modContainer.addConfig(modConfig);
            return modConfig;
        }
    }

    private static net.minecraftforge.fml.config.ModConfig.Type toModConfigType(ModConfig.Type type) {
        return net.minecraftforge.fml.config.ModConfig.Type.values()[type.ordinal()];
    }
}
