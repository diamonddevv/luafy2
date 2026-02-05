package dev.diamond.luafy.script.type.object.game.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.command.RegistrySuggestionProvider;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.type.object.AbstractScriptObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import org.luaj.vm2.LuaTable;

import java.util.Optional;


public class RegistryBlockScriptObject extends AbstractScriptObject<Block> {
    public RegistryBlockScriptObject() {
        super("A block type.", doc -> {

        });
    }

    @Override
    public void toTable(Block obj, LuaTableBuilder builder, LuaScript script) {

    }

    @Override
    public Block toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return null;
    }

    @Override
    public String getArgtypeString() {
        return "Block";
    }

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.of(IdentifierArgument.id());
    }

    @Override
    public Optional<LuaTable> parseCommandToLua(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        return Optional.of(
                provideTable(
                        BuiltInRegistries.BLOCK.getValue(
                                IdentifierArgument.getId(cmdCtx, argName)
                        ),
                        script
                )
        );
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.of(new RegistrySuggestionProvider<>(Registries.BLOCK));
    }
}
