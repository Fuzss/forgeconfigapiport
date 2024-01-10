package fuzs.forgeconfigapiport.fabric.impl.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import fuzs.forgeconfigapiport.fabric.impl.client.commands.arguments.ModIdArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ConfigCommand {
    private static final Dynamic2CommandExceptionType ERROR_NO_CONFIG = new Dynamic2CommandExceptionType((modId, type) -> Component.translatable("commands.config.noconfig", modId, type));

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & StringRepresentable, P extends SharedSuggestionProvider> void register(ConfigCommandContext<T> context, CommandDispatcher<P> dispatcher, BiConsumer<P, Component> feedbackSender) {
        dispatcher.register(LiteralArgumentBuilder.<P>literal(context.name())
                .then(LiteralArgumentBuilder.<P>literal("showfile")
                        .then(RequiredArgumentBuilder.<P, String>argument("mod", ModIdArgument.modIdArgument(modId -> anyModConfigsExist(context, modId)))
                                .then(((RequiredArgumentBuilder<P, ?>) (RequiredArgumentBuilder<?, ?>) RequiredArgumentBuilder.argument("type", enumConstant(context.getType())))
                                        .executes(commandContext -> ConfigCommand.showFile(context, component -> feedbackSender.accept(commandContext.getSource(), component), commandContext.getArgument("mod", String.class), commandContext.getArgument("type", context.getType())))))));
    }

    public static <T extends Enum<T> & StringRepresentable> StringRepresentableArgument<T> enumConstant(Class<? extends T> enumClazz) {
        return new StringRepresentableArgument<>(StringRepresentable.fromEnum(enumClazz::getEnumConstants), enumClazz::getEnumConstants) {};
    }

    private static <T extends Enum<T> & StringRepresentable> boolean anyModConfigsExist(ConfigCommandContext<T> context, String modId) {
        return Stream.of(context.getType().getEnumConstants()).flatMap(type -> context.getConfigFileNames(modId, type).stream()).findAny().isPresent();
    }

    private static <T extends Enum<T> & StringRepresentable> int showFile(ConfigCommandContext<T> context, Consumer<Component> feedbackSender, String modId, T type) throws CommandSyntaxException {
        List<String> configFileNames = context.getConfigFileNames(modId, type);
        if (configFileNames.isEmpty()) {
            throw ERROR_NO_CONFIG.create(modId, type.getSerializedName());
        }
        Component component = configFileNames.stream().map(File::new).map(ConfigCommand::fileComponent).reduce((o1, o2) -> Component.empty().append(o1).append(", ").append(o2)).orElseThrow();
        feedbackSender.accept(Component.translatable("commands.config.getwithtype", modId, type.getSerializedName(), component));
        return configFileNames.size();
    }

    private static Component fileComponent(File file) {
        return Component.literal(file.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
    }

    public interface ConfigCommandContext<T extends Enum<T> & StringRepresentable> {

        String name();

        Class<T> getType();

        List<String> getConfigFileNames(String modId, T type);
    }
}
