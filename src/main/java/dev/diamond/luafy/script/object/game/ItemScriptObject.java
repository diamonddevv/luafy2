package dev.diamond.luafy.script.object.game;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodNames;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


public class ItemScriptObject extends AbstractScriptObject<Item> {
    public static final String PROP_ID = "_id";
    public static final String FUNC_CREATE_STACK = "create_stack";

    public ItemScriptObject() {
        super("An item type.", doc -> {

            doc.addFunction(FUNC_CREATE_STACK, "Creates an items stack of this item type.", args -> {
                args.add("count", Argtypes.INTEGER, "The number of items to create a stack of.");
            }, ScriptObjects.ITEM_STACK);

        });
    }

    @Override
    public void toTable(Item obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_ID, Registries.ITEM.getId(obj).toString());

        builder.add(FUNC_CREATE_STACK, args -> {
            int count = args.arg1().toint();
            ItemStack stack = new ItemStack(obj, count);
            return LuaTableBuilder.provide(b -> ScriptObjects.ITEM_STACK.toTable(stack, b, script));
        });

        builder.addMetamethod(MetamethodNames.TO_STRING, args -> {
            return LuaValue.valueOf(Registries.ITEM.getId(obj).toString());
        });
    }

    @Override
    public Item toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return Registries.ITEM.get(Identifier.of(table.get(PROP_ID).tojstring()));
    }

    @Override
    public String getArgtypeString() {
        return "Item";
    }
}
