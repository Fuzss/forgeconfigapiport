package fuzs.forgeconfigapiport.fabric.impl.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.fabric.impl.client.commands.ClientConfigCommand;
import fuzs.forgeconfigapiport.fabric.impl.network.ConfigSync;
import fuzs.forgeconfigapiport.fabric.impl.network.payload.ConfigFilePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ForgeConfigAPIPortFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerMessages();
        registerEventHandlers();
    }

    private static void registerMessages() {
        ClientConfigurationNetworking.registerGlobalReceiver(ConfigFilePayload.TYPE,
                (ConfigFilePayload payload, ClientConfigurationNetworking.Context context) -> {
                    if (!context.networkHandler().connection.isMemoryConnection()) {
                        ConfigSync.receiveSyncedConfig(payload.contents(), payload.fileName());
                    }
                });
    }

    private static void registerEventHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandBuildContext commandBuildContext) -> {
            ClientConfigCommand.register(commandDispatcher);
        });
        ClientConfigurationConnectionEvents.COMPLETE.register((ClientConfigurationPacketListenerImpl handler, Minecraft client) -> {
            ConfigSync.handleClientLoginSuccess();
        });
        // Reset WORLD type config caches
        ClientPlayConnectionEvents.DISCONNECT.register((ClientPacketListener handler, Minecraft client) -> {
            ModConfigs.getFileMap().values().forEach(config -> {
                if (config.getSpec() instanceof ModConfigSpec spec) {
                    spec.resetCaches(ModConfigSpec.RestartType.WORLD);
                }
            });
        });
    }
}