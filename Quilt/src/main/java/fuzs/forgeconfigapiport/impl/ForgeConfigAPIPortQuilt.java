package fuzs.forgeconfigapiport.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import net.fabricmc.api.EnvType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.ModIdArgument;
import org.apache.commons.lang3.tuple.Pair;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.command.api.ServerArgumentType;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerLoginNetworking;

import java.util.List;

public class ForgeConfigAPIPortQuilt implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        registerServerLoginNetworking();
        registerArgumentTypes();
        registerHandlers();
    }

    private static void registerServerLoginNetworking() {
        ServerLoginConnectionEvents.QUERY_START.register((handler2, server2, sender, synchronizer2) -> {
            final List<Pair<String, FriendlyByteBuf>> pairs = ConfigSync.onSyncConfigs();
            for (Pair<String, FriendlyByteBuf> pair : pairs) {
                synchronizer2.waitFor(server2.submit(() -> sender.sendPacket(ConfigSync.SYNC_CONFIGS_CHANNEL, pair.getValue())));
            }
            synchronizer2.waitFor(server2.submit(() -> sender.sendPacket(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, PacketByteBufs.create())));
        });
        ServerLoginNetworking.registerGlobalReceiver(ConfigSync.SYNC_CONFIGS_CHANNEL, (server1, handler1, understood1, buf1, synchronizer1, responseSender1) -> ConfigSync.onSyncConfigs(server1, handler1, understood1, buf1));
        ServerLoginNetworking.registerGlobalReceiver(ConfigSync.ESTABLISH_MODDED_CONNECTION_CHANNEL, (server, handler, understood, buf, synchronizer, responseSender) -> ConfigSync.onEstablishModdedConnection(server, handler, understood, buf));
    }

    private static void registerArgumentTypes() {
        if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
            // this should work on Quilt even with vanilla clients, but couldn't test yet
            if (!ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("disableConfigCommand")) {
                ServerArgumentType.register(new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "modid"), ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument), originalArg -> {
                    return StringArgumentType.word();
                });
            }
        }
    }

    private static void registerHandlers() {
        ServerLifecycleEvents.STARTING.register(server -> {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server));
        });
        ServerLifecycleEvents.STOPPED.register(server -> {
            ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server));
        });
        CommandRegistrationCallback.EVENT.register((CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) -> {
            if (environment != Commands.CommandSelection.DEDICATED) {
                if (!ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("disableConfigCommand")) {
                    ConfigCommand.register(dispatcher);
                }
            }
        });
    }
}
