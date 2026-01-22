package dev.diamond.luafy.script.object.game;

import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.Block;
import org.luaj.vm2.LuaTable;


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
    public Class<Block> getType() {
        return Block.class;
    }

    @Override
    public String getArgtypeString() {
        return "Block";
    }
}
