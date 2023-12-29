package fuzs.forgeconfigapiport.impl;

import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(ForgeConfigAPIPort.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeConfigAPIPortForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
//        ForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID, ModConfig.Type.SERVER, new ModConfigSpec.Builder().comment("hello world").define("dummy_entry", true).next().build());
        System.out.println(CreativeModeTabs.BUILDING_BLOCKS);
    }
}
