package fuzs.forgeconfigapiport.impl.core;

import fuzs.forgeconfigapiport.impl.util.ServiceProviderHelper;
import net.neoforged.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    void fireConfigLoadingV2(String modId, net.minecraftforge.fml.config.ModConfig modConfig);

    void fireConfigReloadingV2(String modId, net.minecraftforge.fml.config.ModConfig modConfig);

    void fireConfigUnloadingV2(String modId, net.minecraftforge.fml.config.ModConfig modConfig);

    void fireConfigLoadingV3(String modId, ModConfig modConfig);

    void fireConfigReloadingV3(String modId, ModConfig modConfig);

    void fireConfigUnloadingV3(String modId, ModConfig modConfig);

    Stream<String> getAllModIds();

    Path getClientConfigDirectory();

    Path getCommonConfigDirectory();

    Path getDefaultConfigsDirectory();

    boolean isDevelopmentEnvironment();

    Path getConfigDirectory();

    boolean isModLoaded(String modId);
}
