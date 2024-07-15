package fuzs.forgeconfigapiport.fabric.api.neoforge.v4;

import fuzs.forgeconfigapiport.fabric.impl.core.NeoForgeModConfigEventsHolder;
import net.fabricmc.fabric.api.event.Event;
import net.neoforged.fml.config.ModConfig;

import java.util.Objects;

/**
 * Mod config events adapted for Fabric's callback event style.
 * <p>
 * TODO rename to ModConfigEvents, as the Forge class will be removed due to NeoForge's config system being the only one used
 */
public final class NeoForgeModConfigEvents {

    private NeoForgeModConfigEvents() {
        // NO-OP
    }

    /**
     * Access to mod specific loading event.
     *
     * @param modId the mod id to access config event for
     * @return the loading event
     */
    public static Event<Loading> loading(String modId) {
        Objects.requireNonNull(modId, "mod id is null");
        return NeoForgeModConfigEventsHolder.forModId(modId).loading();
    }

    /**
     * Access to mod specific reloading event.
     *
     * @param modId the mod id to access config event for
     * @return the reloading event
     */
    public static Event<Reloading> reloading(String modId) {
        Objects.requireNonNull(modId, "mod id is null");
        return NeoForgeModConfigEventsHolder.forModId(modId).reloading();
    }

    /**
     * Access to mod specific unloading event.
     *
     * @param modId the mod id to access config event for
     * @return the unloading event
     */
    public static Event<Unloading> unloading(String modId) {
        Objects.requireNonNull(modId, "mod id is null");
        return NeoForgeModConfigEventsHolder.forModId(modId).unloading();
    }

    @FunctionalInterface
    public interface Loading {

        /**
         * Called when a config is loaded for the first time.
         * <p>
         * This happens when a config is first opened, and on clients after connecting to a server when default server
         * configs are loaded after not receiving config data from the server.
         *
         * @param config the mod config that is loading
         */
        void onModConfigLoading(ModConfig config);
    }

    @FunctionalInterface
    public interface Reloading {

        /**
         * Called when a config is reloaded.
         * <p>
         * This happens when an update to the config file is found by the
         * {@link com.electronwill.nightconfig.core.file.FileWatcher}, and when a synced config is received on the
         * client from connecting to a server.
         *
         * @param config the mod config that is reloading
         */
        void onModConfigReloading(ModConfig config);
    }

    @FunctionalInterface
    public interface Unloading {

        /**
         * Called when a config is unloaded.
         * <p>
         * This only happens for server configs when the corresponding world is unloaded by either returning to the main
         * menu or disconnecting from a server.
         *
         * @param config the mod config that is unloading
         */
        void onModConfigUnloading(ModConfig config);
    }
}
