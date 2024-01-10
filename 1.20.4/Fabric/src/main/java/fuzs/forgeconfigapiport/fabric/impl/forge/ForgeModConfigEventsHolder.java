package fuzs.forgeconfigapiport.fabric.impl.forge;

import com.google.common.collect.Maps;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeModConfigEvents;
import fuzs.forgeconfigapiport.fabric.impl.util.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

import java.util.Map;

public record ForgeModConfigEventsHolder(String modId, Event<ForgeModConfigEvents.Loading> loading,
                                         Event<ForgeModConfigEvents.Reloading> reloading,
                                         Event<ForgeModConfigEvents.Unloading> unloading) {
    private static final Map<String, ForgeModConfigEventsHolder> HOLDERS_BY_MOD_ID = Maps.newConcurrentMap();

    public static ForgeModConfigEventsHolder forModId(String modId) {
        return HOLDERS_BY_MOD_ID.computeIfAbsent(modId, $ -> {
            Event<ForgeModConfigEvents.Loading> loading = FabricEventFactory.create(ForgeModConfigEvents.Loading.class);
            Event<ForgeModConfigEvents.Reloading> reloading = FabricEventFactory.create(ForgeModConfigEvents.Reloading.class);
            Event<ForgeModConfigEvents.Unloading> unloading = FabricEventFactory.create(ForgeModConfigEvents.Unloading.class);
            return new ForgeModConfigEventsHolder(modId, loading, reloading, unloading);
        });
    }
}
