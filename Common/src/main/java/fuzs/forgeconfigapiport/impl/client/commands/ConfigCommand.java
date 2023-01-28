package fuzs.forgeconfigapiport.impl.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fuzs.forgeconfigapiport.impl.client.commands.arguments.ModIdArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConfigCommand {

    @SuppressWarnings("unchecked")
    public static <T extends SharedSuggestionProvider> void register(CommandDispatcher<T> dispatcher, Function<Class<ModConfig.Type>, ArgumentType<?>> enumArgument, BiConsumer<T, Component> feedbackSender) {
        dispatcher.register(LiteralArgumentBuilder.<T>literal("config")
                .then(LiteralArgumentBuilder.<T>literal("showfile")
                        .then(RequiredArgumentBuilder.<T, String>argument("mod", ModIdArgument.modIdArgument(ConfigTracker.INSTANCE::hasConfigFiles))
                                .then(((RequiredArgumentBuilder<T, ?>) (RequiredArgumentBuilder<?, ?>) RequiredArgumentBuilder.argument("type", enumArgument.apply(ModConfig.Type.class)))
                                        .executes(context -> ConfigCommand.showFile(context, feedbackSender))))));
    }

    private static <T extends SharedSuggestionProvider> int showFile(final CommandContext<T> context, BiConsumer<T, Component> feedbackSender) {
        final String modId = context.getArgument("mod", String.class);
        final ModConfig.Type type = context.getArgument("type", ModConfig.Type.class);
        final List<String> configFileNames = ConfigTracker.INSTANCE.getConfigFileNames(modId, type);
        if (!configFileNames.isEmpty()) {
            Component component = configFileNames.stream().map(File::new).map(ConfigCommand::fileComponent).reduce((o1, o2) -> Component.empty().append(o1).append(", ").append(o2)).orElseThrow();
            feedbackSender.accept(context.getSource(), Component.translatable("commands.config.getwithtype", modId, type, component));
        } else {
            feedbackSender.accept(context.getSource(), Component.translatable("commands.config.noconfig", modId, type));
        }
        return 0;
    }

    private static Component fileComponent(File file) {
        return Component.literal(file.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
    }
}
