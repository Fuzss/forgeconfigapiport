package fuzs.forgeconfigapiport.impl;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ForgeConfigAPIPortFabric implements ModInitializer {
    private static final ResourceLocation BEFORE_PHASE = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "before");
    private static final ResourceLocation AFTER_PHASE = new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "after");

    @Override
    public void onInitialize() {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerArgumentTypes() {
        // Fabric does not filter custom command argument types when syncing them to clients, therefore vanilla clients and
        // clients without this mod will be unable to connect to a server with Forge Config Api Port installed
        // So we disable the command on servers (it does not work anyway as the file name is not clickable in the server console),
        // and allow for disabling the command on dedicated clients via the config to support LAN hosting in a similar scenario
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (!ForgeConfigApiPortConfig.INSTANCE.<Boolean>getValue("disableConfigCommand")) {
                ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "enum"), EnumArgument.class, new EnumArgument.Info());
                ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "modid"), ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
            }
        }
    }

    private static void registerHandlers() {
        ServerLifecycleEvents.SERVER_STARTING.addPhaseOrdering(BEFORE_PHASE, Event.DEFAULT_PHASE);
        ServerLifecycleEvents.SERVER_STARTING.register(BEFORE_PHASE, server -> {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigDirectory(server));
        });
        ServerLifecycleEvents.SERVER_STOPPED.addPhaseOrdering(Event.DEFAULT_PHASE, AFTER_PHASE);
        ServerLifecycleEvents.SERVER_STOPPED.register(AFTER_PHASE, server -> {
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
