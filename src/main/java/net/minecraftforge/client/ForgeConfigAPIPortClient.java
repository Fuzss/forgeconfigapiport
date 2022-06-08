package net.minecraftforge.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.network.client.config.ConfigSyncClient;

public class ForgeConfigAPIPortClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigSyncClient.INSTANCE.clientInit();
    }
}