package fuzs.forgeconfigapiport.impl.core;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;
import java.util.stream.Stream;

public final class QuiltAbstractions implements CommonAbstractions {

    @Override
    public void fireConfigLoading(String modId, ModConfig modConfig) {
        ModConfigEvents.loading(modId).invoker().onModConfigLoading(modConfig);
    }

    @Override
    public void fireConfigReloading(String modId, ModConfig modConfig) {
        ModConfigEvents.reloading(modId).invoker().onModConfigReloading(modConfig);
    }

    @Override
    public void fireConfigUnloading(String modId, ModConfig modConfig) {
        ModConfigEvents.unloading(modId).invoker().onModConfigUnloading(modConfig);
    }

    @Override
    public Stream<String> getAllModIds() {
        return QuiltLoader.getAllMods().stream().map(container -> container.metadata().id());
    }

    @Override
    public Path getClientConfigDirectory() {
        return ForgeConfigPaths.INSTANCE.getClientConfigDirectory();
    }

    @Override
    public Path getCommonConfigDirectory() {
        return ForgeConfigPaths.INSTANCE.getCommonConfigDirectory();
    }

    @Override
    public Path getDefaultConfigsDirectory() {
        return ForgeConfigPaths.INSTANCE.getDefaultConfigsDirectory();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return QuiltLoader.isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return QuiltLoader.getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return QuiltLoader.isModLoaded(modId);
    }
}
