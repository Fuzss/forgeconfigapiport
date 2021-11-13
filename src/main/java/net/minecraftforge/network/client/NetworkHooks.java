package net.minecraftforge.network.client;

import net.minecraft.network.Connection;
import net.minecraftforge.fml.config.ConfigTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkHooks {
    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean isVanillaConnection = true;

    public static void setModdedConnection() {
        isVanillaConnection = false;
    }

    private static void setVanillaConnection() {
        isVanillaConnection = true;
    }

    public static boolean isVanillaConnection(Connection manager)
    {
        return isVanillaConnection;
    }

    public static void handleClientLoginSuccess(Connection manager) {
        if (isVanillaConnection(manager)) {
            LOGGER.info("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            // reset for next server
            setVanillaConnection();
            LOGGER.info("Connected to a modded server.");
        }
    }
}
