package fuzs.forgeconfigapiport.fabric.impl.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fuzs.forgeconfigapiport.fabric.impl.client.commands.arguments.ModIdArgument;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FabricConfigCommand {
    private static final Dynamic2CommandExceptionType ERROR_NO_CONFIG = new Dynamic2CommandExceptionType((modId, type) -> Component.translatable(
            "commands.config.noconfig",
            modId,
            type
    ));

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & StringRepresentable, P extends SharedSuggestionProvider> void register(CommandDispatcher<P> dispatcher, BiConsumer<P, Component> feedbackSender) {
        dispatcher.register(LiteralArgumentBuilder.<P>literal("config")
                .then(RequiredArgumentBuilder.<P, String>argument("mod", new ModIdArgument() {

                            @Override
                            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
                                return SharedSuggestionProvider.suggest(FabricLoader.getInstance()
                                        .getAllMods()
                                        .stream()
                                        .map(container -> container.getMetadata().getId())
                                        .filter(FabricConfigCommand::anyModConfigsExist), builder);
                            }
                        })
                        .then(((RequiredArgumentBuilder<P, ?>) (RequiredArgumentBuilder<?, ?>) RequiredArgumentBuilder.argument(
                                "type",
                                enumConstant(ModConfig.Type.class)
                        )).executes(commandContext -> FabricConfigCommand.showFile(component -> feedbackSender.accept(
                                        commandContext.getSource(),
                                        component
                                ),
                                commandContext.getArgument("mod", String.class),
                                commandContext.getArgument("type", ModConfig.Type.class)
                        )))));
    }

    public static <T extends Enum<T> & StringRepresentable> StringRepresentableArgument<T> enumConstant(Class<? extends T> enumClazz) {
        return new StringRepresentableArgument<>(StringRepresentable.fromEnum(enumClazz::getEnumConstants),
                enumClazz::getEnumConstants
        ) {
            // NO-OP
        };
    }

    private static boolean anyModConfigsExist(String modId) {
        return Stream.of(ModConfig.Type.values())
                .flatMap(type -> ModConfigs.getConfigFileNames(modId, type).stream())
                .findAny()
                .isPresent();
    }

    private static <T extends Enum<T> & StringRepresentable> int showFile(Consumer<Component> feedbackSender, String modId, ModConfig.Type type) throws CommandSyntaxException {
        List<String> configFileNames = ModConfigs.getConfigFileNames(modId, type);
        if (configFileNames.isEmpty()) {
            throw ERROR_NO_CONFIG.create(modId, type.getSerializedName());
        } else {
            configFileNames.stream().map(File::new).map(FabricConfigCommand::fileComponent).forEach((Component component) -> {
                feedbackSender.accept(Component.translatable("commands.config.getwithtype",
                        modId,
                        type.getSerializedName(),
                        component
                ));
            });
            return configFileNames.size();
        }
    }

    private static Component fileComponent(File file) {
        return Component.literal(file.getName())
                .withStyle(ChatFormatting.UNDERLINE)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE,
                        file.getAbsolutePath()
                )));
    }

    public interface ConfigCommandContext<T extends Enum<T> & StringRepresentable> {

        String name();

        Class<T> getType();

        List<String> getConfigFileNames(String modId, T type);
    }
}
