package fuzs.forgeconfigapiport.impl.neoforge;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class CommonAbstractionsImpl {


    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }


    @Nullable
    public static Map<String, Object> getDefaultMap(FileConfig fileConfig) {
        return null;
    }
}
