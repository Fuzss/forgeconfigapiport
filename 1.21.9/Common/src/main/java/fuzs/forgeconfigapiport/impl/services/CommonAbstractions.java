package fuzs.forgeconfigapiport.impl.services;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderLoader.load(CommonAbstractions.class);

    boolean isDevelopmentEnvironment();

    default boolean isDevelopmentEnvironment(String modId) {
        if (!this.isDevelopmentEnvironment()) {
            return false;
        } else {
            return Boolean.getBoolean(modId + ".isDevelopmentEnvironment");
        }
    }
}
