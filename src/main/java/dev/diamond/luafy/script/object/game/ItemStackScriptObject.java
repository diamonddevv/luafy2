package dev.diamond.luafy.script.object.game;

import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaTable;


public class ItemStackScriptObject extends AbstractScriptObject<ItemStack> {
    public ItemStackScriptObject() {
        super("An item stack.", doc -> {

        });
    }

    @Override
    public void toTable(ItemStack obj, LuaTableBuilder builder, LuaScript script) {

    }

    @Override
    public ItemStack toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return null;
    }

    @Override
    public String getArgtypeString() {
        return "ItemStack";
    }
}
