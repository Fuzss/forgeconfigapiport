package fuzs.forgeconfigapiport.fabric.impl.neoforge;

import com.google.common.collect.Maps;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import fuzs.forgeconfigapiport.fabric.impl.util.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

import java.util.Map;

public record NeoForgeModConfigEventsHolder(String modId, Event<NeoForgeModConfigEvents.Loading> loading,
                                            Event<NeoForgeModConfigEvents.Reloading> reloading,
                                            Event<NeoForgeModConfigEvents.Unloading> unloading) {
    private static final Map<String, NeoForgeModConfigEventsHolder> HOLDERS_BY_MOD_ID = Maps.newConcurrentMap();

    public static NeoForgeModConfigEventsHolder forModId(String modId) {
        return HOLDERS_BY_MOD_ID.computeIfAbsent(modId, $ -> {
            Event<NeoForgeModConfigEvents.Loading> loading = FabricEventFactory.create(NeoForgeModConfigEvents.Loading.class);
            Event<NeoForgeModConfigEvents.Reloading> reloading = FabricEventFactory.create(NeoForgeModConfigEvents.Reloading.class);
            Event<NeoForgeModConfigEvents.Unloading> unloading = FabricEventFactory.create(NeoForgeModConfigEvents.Unloading.class);
            return new NeoForgeModConfigEventsHolder(modId, loading, reloading, unloading);
        });
    }
}
