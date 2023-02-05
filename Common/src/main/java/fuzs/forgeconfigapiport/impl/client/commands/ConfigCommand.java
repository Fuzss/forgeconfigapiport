package fuzs.forgeconfigapiport.impl.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import fuzs.forgeconfigapiport.impl.client.commands.arguments.ModIdArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ConfigCommand {
    private static final Dynamic2CommandExceptionType ERROR_NO_CONFIG = new Dynamic2CommandExceptionType((modId, type) -> Component.translatable("commands.config.noconfig", modId, type));

    @SuppressWarnings("unchecked")
    public static <T extends SharedSuggestionProvider> void register(CommandDispatcher<T> dispatcher, BiConsumer<T, Component> feedbackSender) {
        dispatcher.register(LiteralArgumentBuilder.<T>literal("config")
                .then(LiteralArgumentBuilder.<T>literal("showfile")
                        .then(RequiredArgumentBuilder.<T, String>argument("mod", ModIdArgument.modIdArgument(ConfigCommand::anyModConfigsExist))
                                .then(((RequiredArgumentBuilder<T, ?>) (RequiredArgumentBuilder<?, ?>) RequiredArgumentBuilder.argument("type", enumConstant(ModConfig.Type.class)))
                                        .executes(context -> ConfigCommand.showFile(component -> feedbackSender.accept(context.getSource(), component), context.getArgument("mod", String.class), context.getArgument("type", ModConfig.Type.class)))))));
    }

    public static <T extends Enum<T> & StringRepresentable> StringRepresentableArgument<T> enumConstant(Class<? extends T> enumClazz) {
        return new StringRepresentableArgument<>(StringRepresentable.fromEnum(enumClazz::getEnumConstants), enumClazz::getEnumConstants) {};
    }

    private static boolean anyModConfigsExist(String modId) {
        return Stream.of(ModConfig.Type.values()).flatMap(type -> ConfigTracker.INSTANCE.getConfigFileNames(modId, type).stream()).findAny().isPresent();
    }

    private static int showFile(Consumer<Component> feedbackSender, String modId, ModConfig.Type type) throws CommandSyntaxException {
        List<String> configFileNames = ConfigTracker.INSTANCE.getConfigFileNames(modId, type);
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
}
