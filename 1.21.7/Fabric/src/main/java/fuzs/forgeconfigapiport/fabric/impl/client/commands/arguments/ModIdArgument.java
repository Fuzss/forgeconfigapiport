package fuzs.forgeconfigapiport.fabric.impl.client.commands.arguments;

import com.google.common.base.Predicates;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ModIdArgument implements ArgumentType<String> {
    private static final List<String> EXAMPLES = Arrays.asList("fabric", "modmenu");

    private final Predicate<String> filter;

    private ModIdArgument(Predicate<String> filter) {
        this.filter = filter;
    }

    public static ModIdArgument modIdArgument() {
        return new ModIdArgument(Predicates.alwaysTrue());
    }

    public static ModIdArgument modIdArgument(Predicate<String> filter) {
        return new ModIdArgument(filter);
    }

    @Override
    public String parse(final StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(FabricLoader.getInstance()
                .getAllMods()
                .stream()
                .map((ModContainer modContainer) -> modContainer.getMetadata().getId())
                .filter(this.filter), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
