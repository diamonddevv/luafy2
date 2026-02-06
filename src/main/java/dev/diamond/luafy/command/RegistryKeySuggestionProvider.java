package dev.diamond.luafy.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;

public record RegistryKeySuggestionProvider<T>(ResourceKey<Registry<T>> registry) implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

        var reg = ctx.getSource().registryAccess().getOrThrow(registry).value();
        for (var thing : reg) {
            System.out.println(thing.toString() + " - " + reg.getKey(thing).toString());
            builder.suggest(reg.getKey(thing).toString());
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
