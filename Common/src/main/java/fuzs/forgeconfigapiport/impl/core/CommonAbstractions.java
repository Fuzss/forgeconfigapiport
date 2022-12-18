package fuzs.forgeconfigapiport.impl.core;

import fuzs.forgeconfigapiport.impl.util.ServiceProviderUtil;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderUtil.loadServiceProvider(CommonAbstractions.class);

    void fireConfigLoading(String modId, ModConfig modConfig);

    void fireConfigReloading(String modId, ModConfig modConfig);

    void fireConfigUnloading(String modId, ModConfig modConfig);

    Stream<String> getAllModIds();

    Path getClientConfigPath();

    Path getCommonConfigPath();

    Path getDefaultConfigsPath();

    boolean isDevelopmentEnvironment();

    boolean recreateConfigsWhenParsingFails();
}