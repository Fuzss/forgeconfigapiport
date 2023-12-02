package fuzs.forgeconfigapiport.impl.core;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigPaths;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.stream.Stream;

public final class FabricAbstractions implements CommonAbstractions {

    @Override
    public void fireConfigLoadingV2(String modId, ModConfig modConfig) {
        ModConfigEvents.loading(modId).invoker().onModConfigLoading(modConfig);
    }

    @Override
    public void fireConfigReloadingV2(String modId, ModConfig modConfig) {
        ModConfigEvents.reloading(modId).invoker().onModConfigReloading(modConfig);
    }

    @Override
    public void fireConfigUnloadingV2(String modId, ModConfig modConfig) {
        ModConfigEvents.unloading(modId).invoker().onModConfigUnloading(modConfig);
    }

    @Override
    public void fireConfigLoadingV3(String modId, net.neoforged.fml.config.ModConfig modConfig) {
        fuzs.forgeconfigapiport.api.config.v3.ModConfigEvents.loading(modId).invoker().onModConfigLoading(modConfig);
    }

    @Override
    public void fireConfigReloadingV3(String modId, net.neoforged.fml.config.ModConfig modConfig) {
        fuzs.forgeconfigapiport.api.config.v3.ModConfigEvents.reloading(modId).invoker().onModConfigReloading(modConfig);
    }

    @Override
    public void fireConfigUnloadingV3(String modId, net.neoforged.fml.config.ModConfig modConfig) {
        fuzs.forgeconfigapiport.api.config.v3.ModConfigEvents.unloading(modId).invoker().onModConfigUnloading(modConfig);
    }

    @Override
    public Stream<String> getAllModIds() {
        return FabricLoader.getInstance().getAllMods().stream().map(container -> container.getMetadata().getId());
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        return ForgeConfigPaths.INSTANCE.getDefaultConfigsDirectory();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return ForgeConfigPaths.INSTANCE.getConfigDirectory();
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
