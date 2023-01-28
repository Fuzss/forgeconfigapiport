package fuzs.forgeconfigapiport.impl.client.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.core.CommonAbstractions;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ModIdArgument implements ArgumentType<String> {
    private static final List<String> EXAMPLES = Arrays.asList(ForgeConfigAPIPort.MOD_ID, "jei");

    private final Predicate<String> filter;

    private ModIdArgument(Predicate<String> filter) {
        this.filter = filter;
    }

    public static ModIdArgument modIdArgument() {
        return modIdArgument(modId -> true);
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
        return SharedSuggestionProvider.suggest(CommonAbstractions.INSTANCE.getAllModIds().filter(this.filter), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
