package fuzs.forgeconfigapiport.impl.client;

import net.fabricmc.api.ClientModInitializer;
import fuzs.forgeconfigapiport.impl.network.client.config.ConfigSyncClient;

public class ForgeConfigAPIPortFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigSyncClient.INSTANCE.clientInit();
    }
}