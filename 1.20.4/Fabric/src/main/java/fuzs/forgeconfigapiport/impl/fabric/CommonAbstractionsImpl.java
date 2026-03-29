package fuzs.forgeconfigapiport.impl.fabric;

import com.electronwill.nightconfig.core.file.FileConfig;
import fuzs.forgeconfigapiport.fabric.impl.util.ConfigLoadingHelper;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class CommonAbstractionsImpl {


    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }


    @Nullable
    public static Map<String, Object> getDefaultMap(FileConfig fileConfig) {
        return ConfigLoadingHelper.DEFAULT_CONFIG_VALUES.get(fileConfig.getNioPath().getFileName().toString().intern());
    }
}
