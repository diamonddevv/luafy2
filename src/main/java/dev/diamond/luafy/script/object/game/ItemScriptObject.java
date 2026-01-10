package dev.diamond.luafy.script.object.game;

import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;


public class ItemScriptObject extends AbstractScriptObject<Item> {
    public ItemScriptObject() {
        super("An item type.", doc -> {

        });
    }

    @Override
    public void toTable(Item obj, LuaTableBuilder builder, LuaScript script) {

    }

    @Override
    public Item toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return null;
    }

    @Override
    public String getArgtypeString() {
        return "Item";
    }
}
