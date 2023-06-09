package fuzs.forgeconfigapiport.impl.core;

import fuzs.forgeconfigapiport.impl.util.ServiceProviderHelper;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

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
}
