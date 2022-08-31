package net.minecraftforge.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.network.client.config.ConfigSyncClient;
import org.jetbrains.annotations.ApiStatus;

// Forge Config API Port: Fabric client main class
@ApiStatus.Internal
public class ForgeConfigAPIPortClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigSyncClient.INSTANCE.clientInit();
    }
}