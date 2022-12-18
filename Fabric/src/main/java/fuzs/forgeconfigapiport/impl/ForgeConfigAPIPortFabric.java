package fuzs.forgeconfigapiport.impl;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigApiPortConfig;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

public class ForgeConfigAPIPortFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigSync.INSTANCE.init();
        registerArgumentTypes();
        registerHandlers();
        ForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID, ModConfig.Type.COMMON, new ForgeConfigSpec.Builder().push("ha").comment("hello").define("boolean value", true).next().pop().build());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerArgumentTypes() {
        // Fabric does not filter custom command argument types when syncing them to clients, therefore vanilla clients and
        // clients without this mod will be unable to connect to a server with Forge Config Api Port installed
        // So we disable the command on servers (it does not work anyway as the file name is not clickable in the server console),
        // and allow for disabling the command on dedicated clients via the config to support LAN hosting in a similar scenario
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (!ForgeConfigApiPortConfig.INSTANCE.getValue("disableConfigCommand", false)) {
                ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "enum"), EnumArgument.class, new EnumArgument.Info());
                ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(ForgeConfigAPIPort.MOD_ID, "modid"), ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
            }
        }
    }

    private static void registerHandlers() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigPath(server));
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, ForgeConfigPaths.INSTANCE.getServerConfigPath(server));
        });
        CommandRegistrationCallback.EVENT.register((CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) -> {
            if (environment.includeIntegrated) {
                if (!ForgeConfigApiPortConfig.INSTANCE.getValue("disableConfigCommand", false)) {
                    ConfigCommand.register(dispatcher);
                }
            }
        });
    }
}