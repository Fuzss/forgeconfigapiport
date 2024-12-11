package fuzs.forgeconfigapiport.impl.services;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderLoader.load(CommonAbstractions.class);

    boolean isDevelopmentEnvironment();

    default boolean includeTestConfigs() {
        return Boolean.getBoolean(ForgeConfigAPIPort.MOD_ID + ".isDevelopmentEnvironment") &&
                this.isDevelopmentEnvironment();
    }
}
