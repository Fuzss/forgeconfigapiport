package fuzs.forgeconfigapiport.fabric.impl.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.fabric.impl.client.commands.FabricConfigCommand;
import fuzs.forgeconfigapiport.fabric.impl.network.ConfigSync;
import fuzs.forgeconfigapiport.fabric.impl.network.payload.ConfigFilePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.commands.CommandBuildContext;

public class ForgeConfigAPIPortFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerMessages();
        registerEventHandlers();
    }

    private static void registerMessages() {
        ClientConfigurationNetworking.registerGlobalReceiver(ConfigFilePayload.TYPE, (ConfigFilePayload payload, ClientConfigurationNetworking.Context context) -> {
            ConfigSync.receiveSyncedConfig(payload.contents(), payload.fileName());
        });
        ClientConfigurationConnectionEvents.COMPLETE.register((ClientConfigurationPacketListenerImpl handler, Minecraft client) -> {
            ConfigSync.handleClientLoginSuccess();
        });
    }

    private static void registerEventHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            FabricConfigCommand.register(dispatcher, FabricClientCommandSource::sendFeedback);
        });
    }
}