package fuzs.forgeconfigapiport.impl;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgeConfigAPIPort {
    public static final String MOD_ID = "forgeconfigapiport";
    public static final String MOD_NAME = "Forge Config API Port";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier id(String key) {
        return Identifier.fromNamespaceAndPath(MOD_ID, key);
    }
}
