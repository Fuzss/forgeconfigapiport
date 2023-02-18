package net.minecraftforge.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraftforge.client.commands.ConfigCommand;
import net.minecraftforge.network.client.config.ConfigSyncClient;
import org.jetbrains.annotations.ApiStatus;

// Forge Config API Port: Fabric client main class
@ApiStatus.Internal
public class ForgeConfigAPIPortClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigSyncClient.INSTANCE.clientInit();
        registerHandlers();
    }


    private static void registerHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            ConfigCommand.register(dispatcher, FabricClientCommandSource::sendFeedback);
        });
    }
}