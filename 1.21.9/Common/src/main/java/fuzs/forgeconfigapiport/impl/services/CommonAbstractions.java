package fuzs.forgeconfigapiport.impl.services;

import java.util.Optional;

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

    Optional<String> getDisplayName(String modId);
}
