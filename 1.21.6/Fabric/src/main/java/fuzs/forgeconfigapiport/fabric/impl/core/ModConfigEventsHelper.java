package fuzs.forgeconfigapiport.fabric.impl.core;

import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import fuzs.forgeconfigapiport.fabric.impl.util.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.neoforged.fml.config.ModConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModConfigEventsHelper {
    private static final Map<String, Holder> HOLDERS_BY_MOD_ID = new ConcurrentHashMap<>();

    private ModConfigEventsHelper() {
        // NO-OP
    }

    public static void onLoading(ModConfig modConfig) {
        ModConfigEvents.loading(modConfig.getModId()).invoker().onModConfigLoading(modConfig);
    }

    public static void onReloading(ModConfig modConfig) {
        ModConfigEvents.reloading(modConfig.getModId()).invoker().onModConfigReloading(modConfig);
    }

    public static void onUnloading(ModConfig modConfig) {
        ModConfigEvents.unloading(modConfig.getModId()).invoker().onModConfigUnloading(modConfig);
    }

    public static Holder get(String modId) {
        return HOLDERS_BY_MOD_ID.computeIfAbsent(modId, (String modIdX) -> {
            Event<ModConfigEvents.Loading> loading = FabricEventFactory.create(ModConfigEvents.Loading.class);
            Event<ModConfigEvents.Reloading> reloading = FabricEventFactory.create(ModConfigEvents.Reloading.class);
            Event<ModConfigEvents.Unloading> unloading = FabricEventFactory.create(ModConfigEvents.Unloading.class);
            return new Holder(modId, loading, reloading, unloading);
        });
    }

    public record Holder(String modId,
                         Event<ModConfigEvents.Loading> loading,
                         Event<ModConfigEvents.Reloading> reloading,
                         Event<ModConfigEvents.Unloading> unloading) {
    }
}
