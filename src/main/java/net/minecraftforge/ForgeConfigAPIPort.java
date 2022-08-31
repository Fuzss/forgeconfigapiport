package net.minecraftforge;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.network.config.ConfigSync;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Forge Config API Port: Fabric main class
@ApiStatus.Internal
public class ForgeConfigAPIPort implements ModInitializer {
    public static final String MOD_ID = "forgeconfigapiport";
    public static final String MOD_NAME = "Forge Config API Port";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    /**
     * @deprecated outdated Log4j marker, use your own marker!
     */
    @Deprecated(forRemoval = true)
    public static final Marker CORE = MarkerManager.getMarker("CORE");

    @Override
    public void onInitialize() {
        ConfigSync.INSTANCE.init();
        FMLConfig.load();
        registerArgumentTypes();
        registerHandlers();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerArgumentTypes() {
        // don't add on servers as command is useless there anyway and serializing arguments will crash connecting vanilla clients
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
        ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(MOD_ID, "enum"), EnumArgument.class, new EnumArgument.Info());
        ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(MOD_ID, "modid"), ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
    }

    private static void registerHandlers() {
        CommandRegistrationCallback.EVENT.register((CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) -> {
            if (environment.includeIntegrated) ConfigCommand.register(dispatcher);
        });
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleHooks::handleServerAboutToStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleHooks::handleServerStopped);
    }
}
