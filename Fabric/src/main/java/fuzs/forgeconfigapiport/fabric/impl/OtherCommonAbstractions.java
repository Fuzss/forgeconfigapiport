package fuzs.forgeconfigapiport.fabric.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public interface OtherCommonAbstractions {
    @ExpectPlatform
    static Path getDefaultConfigsDirectory() {
        throw new RuntimeException();
    }

    @ExpectPlatform
    static Path getConfigDirectory() {
        throw new RuntimeException();
    }

    @ExpectPlatform
    static Path getGameDirectory() {
        throw new RuntimeException();
    }
}
