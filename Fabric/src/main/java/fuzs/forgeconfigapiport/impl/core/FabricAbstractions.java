package fuzs.forgeconfigapiport.impl.core;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigApiPortConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.stream.Stream;

public final class FabricAbstractions implements CommonAbstractions {

    @Override
    public void fireConfigLoading(String modId, ModConfig modConfig) {
        ModConfigEvents.loading(modId).invoker().onModConfigLoading(modConfig);
        net.minecraftforge.api.fml.event.config.ModConfigEvents.loading(modId).invoker().onModConfigLoading(modConfig);
        ModConfigEvent.LOADING.invoker().onModConfigLoading(modConfig);
    }

    @Override
    public void fireConfigReloading(String modId, ModConfig modConfig) {
        ModConfigEvents.reloading(modId).invoker().onModConfigReloading(modConfig);
        net.minecraftforge.api.fml.event.config.ModConfigEvents.reloading(modId).invoker().onModConfigReloading(modConfig);
        ModConfigEvent.RELOADING.invoker().onModConfigReloading(modConfig);
    }

    @Override
    public void fireConfigUnloading(String modId, ModConfig modConfig) {
        ModConfigEvents.unloading(modId).invoker().onModConfigUnloading(modConfig);
        net.minecraftforge.api.fml.event.config.ModConfigEvents.unloading(modId).invoker().onModConfigUnloading(modConfig);
    }

    @Override
    public Stream<String> getAllModIds() {
        return FabricLoader.getInstance().getAllMods().stream().map(container -> container.getMetadata().getId());
    }

    @Override
    public Path getClientConfigPath() {
        return ForgeConfigPaths.INSTANCE.getClientConfigPath();
    }

    @Override
    public Path getCommonConfigPath() {
        return ForgeConfigPaths.INSTANCE.getCommonConfigPath();
    }

    @Override
    public Path getDefaultConfigsPath() {
        return ForgeConfigPaths.INSTANCE.getDefaultConfigsPath();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean recreateConfigsWhenParsingFails() {
        return ForgeConfigApiPortConfig.INSTANCE.getValue("recreateConfigsWhenParsingFails", true);
    }
}
