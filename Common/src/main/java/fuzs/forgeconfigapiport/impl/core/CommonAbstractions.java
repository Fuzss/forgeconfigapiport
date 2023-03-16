package fuzs.forgeconfigapiport.impl.core;

import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = loadServiceProvider(CommonAbstractions.class);

    void fireConfigLoading(String modId, ModConfig modConfig);

    void fireConfigReloading(String modId, ModConfig modConfig);

    void fireConfigUnloading(String modId, ModConfig modConfig);

    Stream<String> getAllModIds();

    Path getClientConfigDirectory();

    Path getCommonConfigDirectory();

    Path getDefaultConfigsDirectory();

    boolean isDevelopmentEnvironment();

    Path getConfigDirectory();

    boolean isModLoaded(String modId);

    private static <T> T loadServiceProvider(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
