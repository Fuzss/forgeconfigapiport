package net.minecraftforge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.network.config.ConfigSync;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Forge Config API Port: Fabric main class
@ApiStatus.Internal
public class ForgeConfigAPIPort implements ModInitializer {
    public static final String MOD_ID = "forgeconfigapiport";
    public static final String MOD_NAME = "Forge Config API Port";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    /**
     * @deprecated outdated Log4j marker, use your own marker!
     */
    @Deprecated(forRemoval = true)
    public static final Marker CORE = MarkerManager.getMarker("CORE");

    @Override
    public void onInitialize() {
        ConfigSync.INSTANCE.init();
        FMLConfig.load();
        registerHandlers();
    }

    private static void registerHandlers() {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleHooks::handleServerAboutToStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleHooks::handleServerStopped);
    }
}
