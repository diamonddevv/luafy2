package dev.diamond.luafy.script.type;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class IdentifierStringAlias extends StringAlias<Identifier> {
    @Override
    public Identifier parse(String s, LuaScript script) {
        return Identifier.parse(s);
    }

    @Override
    public String serialise(Identifier identifier, LuaScript script) {
        return identifier.toString();
    }

    @Override
    public String getArgtypeString() {
        return "Identifier";
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.empty();
    }

}
