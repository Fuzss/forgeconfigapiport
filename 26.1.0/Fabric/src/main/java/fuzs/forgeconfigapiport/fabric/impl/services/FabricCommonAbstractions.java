package fuzs.forgeconfigapiport.fabric.impl.services;

import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricCommonAbstractions implements CommonAbstractions {

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
