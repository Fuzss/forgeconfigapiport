package fuzs.forgeconfigapiport.impl.client;

import fuzs.forgeconfigapiport.impl.network.client.config.ConfigSyncClient;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;

public class ForgeConfigAPIPortFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerClientLoginNetworking();
    }

    private static void registerClientLoginNetworking() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (client1, handler1, buf1, listenerAdder1) -> ConfigSyncClient.onSyncConfigs(client1, handler1, buf1));
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (client, handler, buf, listenerAdder) -> ConfigSyncClient.onEstablishModdedConnection(client, handler, buf));
    }
}