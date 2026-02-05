package dev.diamond.luafy.script.type.enumeration;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.diamond.luafy.script.type.Argtype;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.autodoc.SimpleAutodocumentable;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.LuaString;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ScriptEnum<E extends Enum<E>> implements SimpleAutodocumentable, Argtype<LuaString, String> {

    private final Class<E> enumClass;

    public ScriptEnum(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    private String getEnumName() {
        return enumClass.getSimpleName();
    }

    public Collection<String> getEnumKeys() {
        return Arrays.stream(enumClass.getEnumConstants()).map(E::name).collect(Collectors.toSet());
    }

    public String toKey(E e) {
        return e.name();
    }

    public E fromKey(String key) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(e -> Objects.equals(key, e.name())).findFirst().orElseThrow();
    }

    @Override
    public String generateAutodocString() {
        StringBuilder b = new StringBuilder();

        return b.toString();
    }

    @Override
    public String getArgtypeString() {
        return getEnumName();
    }

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.of(StringArgumentType.word());
    }

    @Override
    public Optional<LuaString> parseCommand(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        return Argtypes.STRING.parseCommand(cmdCtx, argName, script);
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.of(new EnumSuggestionProvider<>(this.enumClass));
    }

    private record EnumSuggestionProvider<E extends Enum<E>>(Class<E> clazz) implements SuggestionProvider<CommandSourceStack> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (var constant : clazz.getEnumConstants()) {
                builder.suggest(constant.name());
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }
}
