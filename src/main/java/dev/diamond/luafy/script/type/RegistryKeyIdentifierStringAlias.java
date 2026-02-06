package dev.diamond.luafy.script.type;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.command.RegistryKeySuggestionProvider;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;
import java.util.Optional;

public class RegistryKeyIdentifierStringAlias<T> extends StringAlias<T> {

    private final ResourceKey<Registry<T>> registry;
    private final String identifierType;

    public RegistryKeyIdentifierStringAlias(ResourceKey<Registry<T>> registry, String identifierType) {
        this.registry = registry;
        this.identifierType = identifierType;
    }

    @Override
    public String getArgtypeString() {
        return identifierType + "Identifier";
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.of(new RegistryKeySuggestionProvider<>(this.registry));
    }

    @Override
    public T parse(String s, LuaScript script) {
        return script.getRegistry(registry).getValue(Identifier.parse(s));
    }

    @Override
    public String serialise(T t, LuaScript script) {
        return Objects.requireNonNull(script.getRegistry(registry).getKey(t)).toString();
    }
}
