package fuzs.forgeconfigapiport.fabric.impl.services;

import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Optional;

public final class FabricCommonAbstractions implements CommonAbstractions {

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Optional<String> getDisplayName(String modId) {
        return FabricLoader.getInstance()
                .getModContainer(modId)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getName);
    }
}
