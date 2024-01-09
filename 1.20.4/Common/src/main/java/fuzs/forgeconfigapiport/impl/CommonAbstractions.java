package fuzs.forgeconfigapiport.impl;

import com.electronwill.nightconfig.core.file.FileConfig;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class CommonAbstractions {


    @ExpectPlatform
    public static boolean isDevelopmentEnvironment() {
        throw new RuntimeException();
    }


    @Nullable
    @ExpectPlatform
    public static Map<String, Object> getDefaultMap(FileConfig fileConfig) {
        throw new RuntimeException();
    }
}
