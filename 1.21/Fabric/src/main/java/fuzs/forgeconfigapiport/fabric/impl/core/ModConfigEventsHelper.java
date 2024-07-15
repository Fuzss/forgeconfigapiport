package fuzs.forgeconfigapiport.fabric.impl.core;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeModConfigEvents;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.neoforged.fml.config.ModConfig;

import java.util.function.Consumer;

public final class ModConfigEventsHelper {

    private ModConfigEventsHelper() {
        // NO-OP
    }

    public static Consumer<ModConfig> loading() {
        return (ModConfig modConfig) -> {
            NeoForgeModConfigEvents.loading(modConfig.getModId()).invoker().onModConfigLoading(modConfig);
            // call Forge event in addition if applicable, everyone should migrate to the NeoForge events though
            if (modConfig.modConfig != null) {
                ForgeModConfigEvents.loading(modConfig.getModId()).invoker().onModConfigLoading(modConfig.modConfig);
            }
        };
    }

    public static Consumer<ModConfig> reloading() {
        return (ModConfig modConfig) -> {
            NeoForgeModConfigEvents.reloading(modConfig.getModId()).invoker().onModConfigReloading(modConfig);
            // call Forge event in addition if applicable, everyone should migrate to the NeoForge events though
            if (modConfig.modConfig != null) {
                ForgeModConfigEvents.reloading(modConfig.getModId())
                        .invoker()
                        .onModConfigReloading(modConfig.modConfig);
            }
        };
    }

    public static Consumer<ModConfig> unloading() {
        return (ModConfig modConfig) -> {
            NeoForgeModConfigEvents.unloading(modConfig.getModId()).invoker().onModConfigUnloading(modConfig);
            // call Forge event in addition if applicable, everyone should migrate to the NeoForge events though
            if (modConfig.modConfig != null) {
                ForgeModConfigEvents.unloading(modConfig.getModId())
                        .invoker()
                        .onModConfigUnloading(modConfig.modConfig);
            }
        };
    }
}
