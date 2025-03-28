package fuzs.forgeconfigapiport.forge.impl;

import fuzs.forgeconfigapiport.forge.api.v5.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(ForgeConfigAPIPort.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeConfigAPIPortForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        if (CommonAbstractions.INSTANCE.isDevelopmentEnvironment(ForgeConfigAPIPort.MOD_ID)) {
            NeoForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID,
                    ModConfig.Type.SERVER,
                    new ModConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build(),
                    "forgeconfigapiport-server-neoforge.toml");
            ModLoadingContext.get()
                    .registerConfig(ModConfig.Type.SERVER,
                            new ForgeConfigSpec.Builder().comment("hello world")
                                    .define("dummy_entry", true)
                                    .next()
                                    .build(),
                            "forgeconfigapiport-server-forge.toml");
        }
    }
}
