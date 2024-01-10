package fuzs.forgeconfigapiport.fabric.api.forge.v4;

import fuzs.forgeconfigapiport.fabric.impl.forge.ForgeModConfigEventsHolder;
import net.fabricmc.fabric.api.event.Event;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Objects;

/**
 * Mod config events adapted for Fabric's callback event style.
 */
public final class ForgeModConfigEvents {

    private ForgeModConfigEvents() {

    }

    /**
     * Access to mod specific loading event.
     *
     * @param modId the mod id to access config event for
     * @return the {@link Loading} event
     */
    public static Event<Loading> loading(String modId) {
        Objects.requireNonNull(modId, "mod id is null");
        return ForgeModConfigEventsHolder.forModId(modId).loading();
    }

    /**
     * Access to mod specific reloading event.
     *
     * @param modId the mod id to access config event for
     * @return the {@link Reloading} event
     */
    public static Event<Reloading> reloading(String modId) {
        Objects.requireNonNull(modId, "mod id is null");
        return ForgeModConfigEventsHolder.forModId(modId).reloading();
    }

    /**
     * Access to mod specific unloading event.
     *
     * @param modId the mod id to access config event for
     * @return the {@link Unloading} event
     */
    public static Event<Unloading> unloading(String modId) {
        Objects.requireNonNull(modId, "mod id is null");
        return ForgeModConfigEventsHolder.forModId(modId).unloading();
    }

    @FunctionalInterface
    public interface Loading {

        /**
         * Called when a config is loaded for the first time.
         * 
         * @param config the mod config that is loading
         */
        void onModConfigLoading(ModConfig config);
    }

    /**
     * Called when a config is reloaded.
     * <p>This happens when an update to the config file is found by the {@link com.electronwill.nightconfig.core.file.FileWatcher}, and when a synced config is received from connecting to a server.
     */
    @FunctionalInterface
    public interface Reloading {

        /**
         * @param config the mod config that is reloading
         */
        void onModConfigReloading(ModConfig config);
    }

    @FunctionalInterface
    public interface Unloading {

        /**
         * Called when a config is unloaded.
         * <p>This only happens for server configs when the corresponding world is unloaded by either returning to the main menu or disconnecting from a server.
         * 
         * @param config the mod config that is unloading
         */
        void onModConfigUnloading(ModConfig config);
    }
}
