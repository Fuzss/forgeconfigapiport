package fuzs.forgeconfigapiport.impl.client;

import fuzs.forgeconfigapiport.impl.network.client.config.ConfigSyncClient;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientLoginNetworking;

public class ForgeConfigAPIPortQuiltClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        registerClientLoginNetworking();
    }

    private static void registerClientLoginNetworking() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (client, handler, buf, buf2) -> ConfigSyncClient.onSyncConfigs(client, handler, buf));
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (client, handler, buf, buf2) -> ConfigSyncClient.onEstablishModdedConnection(client, handler, buf));
    }
}