package fuzs.forgeconfigapiport.impl.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.impl.network.client.config.ConfigSyncClient;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import fuzs.forgeconfigapiport.impl.client.commands.ConfigCommand;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

public class ForgeConfigAPIPortFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerMessages();
        registerHandlers();
    }

    private static void registerMessages() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (Minecraft client1, ClientHandshakePacketListenerImpl handler1, FriendlyByteBuf buf1, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder1) -> {
            return ConfigSyncClient.onSyncConfigs(client1, handler1, buf1);
        });
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (client, handler, buf, listenerAdder) -> ConfigSyncClient.onEstablishModdedConnection(client, handler, buf));
    }

    private static void registerHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            ConfigCommand.register(dispatcher, FabricClientCommandSource::sendFeedback);
        });
    }
}