package fuzs.forgeconfigapiport.impl.services;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderLoader.load(CommonAbstractions.class);

    boolean isDevelopmentEnvironment();

    default boolean includeTestConfigs() {
        return false;
    }
}
