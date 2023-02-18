package net.minecraftforge.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConfigCommand {

    @SuppressWarnings("unchecked")
    public static <T extends SharedSuggestionProvider> void register(CommandDispatcher<T> dispatcher, BiConsumer<T, Component> feedbackSender) {
        dispatcher.register(LiteralArgumentBuilder.<T>literal("config")
                .then(LiteralArgumentBuilder.<T>literal("showfile")
                        .then(RequiredArgumentBuilder.<T, String>argument("mod", ModIdArgument.modIdArgument())
                                .then(((RequiredArgumentBuilder<T, ?>) (RequiredArgumentBuilder<?, ?>) RequiredArgumentBuilder.argument("type", EnumArgument.enumArgument(ModConfig.Type.class)))
                                        .executes(context -> ConfigCommand.showFile(component -> feedbackSender.accept(context.getSource(), component), context.getArgument("mod", String.class), context.getArgument("type", ModConfig.Type.class)))))));
    }

    private static int showFile(Consumer<Component> feedbackSender, String modId, ModConfig.Type type) throws CommandSyntaxException {
        final String configFileName = ConfigTracker.INSTANCE.getConfigFileName(modId, type);
        if (configFileName != null) {
            File f = new File(configFileName);
            feedbackSender.accept(Component.translatable("commands.config.getwithtype",
                    modId, type,
                    Component.literal(f.getName()).withStyle(ChatFormatting.UNDERLINE).
                            withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath())))
            ));
        } else {
            feedbackSender.accept(Component.translatable("commands.config.noconfig", modId, type));
        }
        return 0;
    }
}
