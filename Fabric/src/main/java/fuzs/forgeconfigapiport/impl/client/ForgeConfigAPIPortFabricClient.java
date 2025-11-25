package fuzs.forgeconfigapiport.impl.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.impl.client.commands.ConfigCommand;
import fuzs.forgeconfigapiport.impl.network.client.config.ConfigSyncClient;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;

public class ForgeConfigAPIPortFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerMessages();
        registerHandlers();
    }

    private static void registerMessages() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (client1, handler1, buf1, listenerAdder1) -> ConfigSyncClient.onSyncConfigs(client1, handler1, buf1));
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (client, handler, buf, listenerAdder) -> ConfigSyncClient.onEstablishModdedConnection(client, handler, buf));
    }

    private static void registerHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            ConfigCommand.register(dispatcher, FabricClientCommandSource::sendFeedback);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((ClientPacketListener handler, Minecraft client) -> {
            // Unload SERVER configs only when disconnecting from a remote server
            if (handler.getConnection() != null && !handler.getConnection().isMemoryConnection()) {
                ConfigSync.unloadSyncedConfig();
            }
        });
    }
}
