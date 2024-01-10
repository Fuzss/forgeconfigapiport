package fuzs.forgeconfigapiport.fabric.impl.config.legacy;

import com.google.common.collect.Maps;
import fuzs.forgeconfigapiport.api.config.v3.ModConfigEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Map;

/**
 * internal mod specific event storage
 *
 * @param modId     the mod
 * @param loading   loading event
 * @param reloading reloading event
 */
public record ModConfigEventsHolderV3(String modId, Event<ModConfigEvents.Loading> loading,
                                      Event<ModConfigEvents.Reloading> reloading, Event<ModConfigEvents.Unloading> unloading) {
    /**
     * internal storage for mod specific config events
     */
    private static final Map<String, ModConfigEventsHolderV3> MOD_SPECIFIC_EVENT_HOLDERS = Maps.newConcurrentMap();

    /**
     * internal access to mod specific config events
     *
     * <p>the method is synchronized as access from different threads is possible (e.g. the config watcher thread)
     *
     * @param modId the mod id to access config events for
     * @return access to a holder with both mod specific {@link ModConfigEvents.Loading} and {@link ModConfigEvents.Reloading} events
     */
    public static ModConfigEventsHolderV3 modSpecific(String modId) {
        return MOD_SPECIFIC_EVENT_HOLDERS.computeIfAbsent(modId, ModConfigEventsHolderV3::create);
    }

    /**
     * creates a new holder duh
     *
     * @param modId the mod
     * @return holder with newly created events
     */
    private static ModConfigEventsHolderV3 create(String modId) {
        Event<ModConfigEvents.Loading> loading = EventFactory.createArrayBacked(ModConfigEvents.Loading.class, listeners -> config -> {
            for (ModConfigEvents.Loading event : listeners) {
                event.onModConfigLoading(config);
            }
        });
        Event<ModConfigEvents.Reloading> reloading = EventFactory.createArrayBacked(ModConfigEvents.Reloading.class, listeners -> config -> {
            for (ModConfigEvents.Reloading event : listeners) {
                event.onModConfigReloading(config);
            }
        });
        Event<ModConfigEvents.Unloading> unloading = EventFactory.createArrayBacked(ModConfigEvents.Unloading.class, listeners -> config -> {
            for (ModConfigEvents.Unloading event : listeners) {
                event.onModConfigUnloading(config);
            }
        });
        return new ModConfigEventsHolderV3(modId, loading, reloading, unloading);
    }
}
