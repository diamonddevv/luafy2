package dev.diamond.luafy.script.type;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

import java.util.Optional;

public abstract class StringAlias<T> implements Argtype<LuaString, T> {

    public abstract T parse(String s, LuaScript script);
    public abstract String serialise(T t, LuaScript script);

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.of(StringArgumentType.word());
    }

    @Override
    public Optional<LuaString> parseCommandToLua(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        return Optional.of(LuaValue.valueOf(StringArgumentType.getString(cmdCtx, argName)));
    }
}
