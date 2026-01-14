package dev.diamond.luafy.script.object.game;

import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.block.Block;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;


public class BlockScriptObject extends AbstractScriptObject<Block> {
    public BlockScriptObject() {
        super("A block type.", doc -> {

        });
    }

    @Override
    public void toTable(Block obj, LuaTableBuilder builder, LuaScript script) {

    }

    @Override
    public Block toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return null;
    }

    @Override
    public String getArgtypeString() {
        return "Block";
    }
}
