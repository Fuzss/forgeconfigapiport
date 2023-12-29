package fuzs.forgeconfigapiport.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgeConfigAPIPort {
    public static final String MOD_ID = "forgeconfigapiport";
    public static final String MOD_NAME = "Forge Config API Port";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    static {
        System.out.println(CreativeModeTabs.BUILDING_BLOCKS);
    }

    public static ResourceLocation id(String key) {
        return new ResourceLocation(MOD_ID, key);
    }
}
