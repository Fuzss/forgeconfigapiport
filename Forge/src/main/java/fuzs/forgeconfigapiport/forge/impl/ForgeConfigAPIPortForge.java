package fuzs.forgeconfigapiport.forge.impl;

import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod("forgeconfigapiport")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeConfigAPIPortForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        NeoForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID, ModConfig.Type.SERVER, new ModConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build());
    }
}
