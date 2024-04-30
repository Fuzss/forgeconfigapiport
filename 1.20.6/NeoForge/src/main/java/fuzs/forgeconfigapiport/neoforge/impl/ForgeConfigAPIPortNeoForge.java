package fuzs.forgeconfigapiport.neoforge.impl;

import fuzs.forgeconfigapiport.impl.CommonAbstractions;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(ForgeConfigAPIPort.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ForgeConfigAPIPortNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        if (CommonAbstractions.isDevelopmentEnvironment() && CommonAbstractions.includeTestConfigs()) {
            ModList.get()
                    .getModContainerById(ForgeConfigAPIPort.MOD_ID)
                    .ifPresent(modContainer -> modContainer.registerConfig(ModConfig.Type.SERVER,
                            new ModConfigSpec.Builder().comment("hello world")
                                    .define("dummy_entry", true)
                                    .next()
                                    .build(),
                            "forgeconfigapiport-server-neoforge.toml"
                    ));
            ForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID,
                    ModConfig.Type.SERVER,
                    new ForgeConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build(),
                    "forgeconfigapiport-server-forge.toml"
            );
        }
    }
}
