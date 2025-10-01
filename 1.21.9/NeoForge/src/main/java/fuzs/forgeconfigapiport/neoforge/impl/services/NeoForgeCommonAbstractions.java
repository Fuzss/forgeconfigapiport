package fuzs.forgeconfigapiport.neoforge.impl.services;

import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.neoforged.fml.loading.FMLEnvironment;

public final class NeoForgeCommonAbstractions implements CommonAbstractions {

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }
}
