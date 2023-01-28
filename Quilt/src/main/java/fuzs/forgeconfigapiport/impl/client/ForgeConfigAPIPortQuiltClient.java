package fuzs.forgeconfigapiport.impl.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.impl.network.client.config.ConfigSyncClient;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import fuzs.forgeconfigapiport.impl.client.commands.ConfigCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;
import org.quiltmc.qsl.networking.api.client.ClientLoginNetworking;

public class ForgeConfigAPIPortQuiltClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        registerMessages();
        registerHandlers();
    }

    private static void registerMessages() {
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (client1, handler1, buf1, listenerAdder1) -> ConfigSyncClient.onSyncConfigs(client1, handler1, buf1));
        ClientLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (client, handler, buf, listenerAdder) -> ConfigSyncClient.onEstablishModdedConnection(client, handler, buf));
    }

    private static void registerHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<QuiltClientCommandSource> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection environment) -> {
            ConfigCommand.register(dispatcher, QuiltClientCommandSource::sendFeedback);
        });
    }
}