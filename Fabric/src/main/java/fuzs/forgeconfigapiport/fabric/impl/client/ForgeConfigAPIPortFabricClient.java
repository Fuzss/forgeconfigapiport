package fuzs.forgeconfigapiport.fabric.impl.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.fabric.impl.client.commands.ConfigCommand;
import fuzs.forgeconfigapiport.fabric.impl.network.ConfigSync;
import fuzs.forgeconfigapiport.fabric.impl.network.payload.ConfigFilePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

public class ForgeConfigAPIPortFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerMessages();
        registerEventHandlers();
    }

    private static void registerMessages() {
        ClientConfigurationNetworking.registerGlobalReceiver(ConfigFilePayload.TYPE, (payload, context) -> {
            ConfigSync.receiveSyncedConfig(payload.contents(), payload.fileName());
        });
        ClientConfigurationConnectionEvents.READY.register((handler, client) -> {
            ConfigSync.handleClientLoginSuccess();
        });
    }

    private static void registerEventHandlers() {
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