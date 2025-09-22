package fuzs.forgeconfigapiport.neoforge.impl.services;

import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.Optional;

public final class NeoForgeCommonAbstractions implements CommonAbstractions {

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public Optional<String> getDisplayName(String modId) {
        return ModList.get().getModContainerById(modId).map(ModContainer::getModInfo).map(IModInfo::getDisplayName);
    }
}
