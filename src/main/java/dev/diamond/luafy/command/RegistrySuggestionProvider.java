package dev.diamond.luafy.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record RegistrySuggestionProvider<T>(Registry<T> registry) implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

        for (var thing : registry) {
            builder.suggest(registry.getKey(thing).toString());
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
