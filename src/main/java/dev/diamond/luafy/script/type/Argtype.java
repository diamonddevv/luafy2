package dev.diamond.luafy.script.type;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.jspecify.annotations.Nullable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;
import java.util.function.BiFunction;

public interface Argtype<T extends LuaValue, S> {
    String getArgtypeString();
    Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx);
    Optional<T> parseCommand(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script);
    Optional<SuggestionProvider<CommandSourceStack>> suggest();

    static <T extends LuaValue, S> Argtype<T, S> of(String arg, @Nullable ArgumentType<S> commandArgumentType, @Nullable BiFunction<CommandContext<?>, String, T> parser, @Nullable SuggestionProvider<CommandSourceStack> suggestionProvider) {
        return new Argtype<>() {
            @Override
            public String getArgtypeString() {
                return arg;
            }

            @Override
            public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
                return Optional.ofNullable(commandArgumentType);
            }

            @Override
            public Optional<T> parseCommand(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
                if (parser == null) return Optional.empty();
                return Optional.ofNullable(parser.apply(cmdCtx, argName));
            }

            @Override
            public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
                return Optional.ofNullable(suggestionProvider);
            }
        };
    }

    static <T extends LuaValue, S> Argtype<T, S> of(String arg) {
        return of(arg, null, null, null);
    }
}
