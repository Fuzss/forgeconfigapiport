package fuzs.forgeconfigapiport.fabric.impl.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.fabric.impl.client.commands.ConfigCommand;
import fuzs.forgeconfigapiport.fabric.impl.network.client.config.ConfigSyncClient;
import fuzs.forgeconfigapiport.fabric.impl.network.config.ConfigSync;
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
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
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
            ConfigCommand.register(new ConfigCommand.ConfigCommandContext<ModConfig.Type>() {

                @Override
                public String name() {
                    return "forgeconfig";
                }

                @Override
                public Class<ModConfig.Type> getType() {
                    return ModConfig.Type.class;
                }

                @Override
                public List<String> getConfigFileNames(String modId, ModConfig.Type type) {
                    return ConfigTracker.INSTANCE.getConfigFileNames(modId, type);
                }
            }, dispatcher, FabricClientCommandSource::sendFeedback);
            ConfigCommand.register(new ConfigCommand.ConfigCommandContext<net.neoforged.fml.config.ModConfig.Type>() {

                @Override
                public String name() {
                    return "neoforgeconfig";
                }

                @Override
                public Class<net.neoforged.fml.config.ModConfig.Type> getType() {
                    return net.neoforged.fml.config.ModConfig.Type.class;
                }

                @Override
                public List<String> getConfigFileNames(String modId, net.neoforged.fml.config.ModConfig.Type type) {
                    return net.neoforged.fml.config.ConfigTracker.INSTANCE.getConfigFileNames(modId, type);
                }
            }, dispatcher, FabricClientCommandSource::sendFeedback);
        });
    }
}