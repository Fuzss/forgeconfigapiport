package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigRegistry;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ForgeConfigRegistryImpl implements ForgeConfigRegistry {

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec) {
        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        if (!modContainer.getNamespace().equals(modId)) {
            throw new IllegalStateException("active namespace '%s' does not match mod id '%s'".formatted(modContainer.getNamespace(), modId));
        } else {
            try {
                // FIXME for 1.21 when Forge classes have been removed from Forge Config Api Port, so the actual Forge constructor becomes accessible from here
                MethodType methodType = MethodType.methodType(void.class, ModConfig.Type.class, net.minecraftforge.fml.config.IConfigSpec.class, ModContainer.class);
                MethodHandle methodHandle = MethodHandles.publicLookup().findConstructor(ModConfig.class, methodType);
                ModConfig modConfig = (ModConfig) methodHandle.invoke(toModConfigType(type), new ForwardingConfigSpec<>(spec), modContainer);
                modContainer.addConfig(modConfig);
                return modConfig;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ModConfig register(String modId, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        if (!modContainer.getNamespace().equals(modId)) {
            throw new IllegalStateException("active namespace '%s' does not match mod id '%s'".formatted(modContainer.getNamespace(), modId));
        } else {
            try {
                // FIXME for 1.21 when Forge classes have been removed from Forge Config Api Port, so the actual Forge constructor becomes accessible from here
                MethodType methodType = MethodType.methodType(void.class, ModConfig.Type.class, net.minecraftforge.fml.config.IConfigSpec.class, ModContainer.class, String.class);
                MethodHandle methodHandle = MethodHandles.publicLookup().findConstructor(ModConfig.class, methodType);
                ModConfig modConfig = (ModConfig) methodHandle.invoke(toModConfigType(type), new ForwardingConfigSpec<>(spec), modContainer, fileName);
                modContainer.addConfig(modConfig);
                return modConfig;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static net.minecraftforge.fml.config.ModConfig.Type toModConfigType(ModConfig.Type type) {
        return net.minecraftforge.fml.config.ModConfig.Type.values()[type.ordinal()];
    }
}
