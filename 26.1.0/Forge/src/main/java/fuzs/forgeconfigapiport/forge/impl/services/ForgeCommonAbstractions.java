package fuzs.forgeconfigapiport.forge.impl.services;

import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class ForgeCommonAbstractions implements CommonAbstractions {

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }
}
