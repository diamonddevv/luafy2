package dev.diamond.luafy.script.object.game;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


public class ItemStackScriptObject extends AbstractScriptObject<ItemStack> {
    public static final String PROP_UNSERIALIZABLE_POINTER = "_ptr";

    public static final String FUNC_COUNT = "get_count";
    public static final String FUNC_COUNT_SET = "set_count";
    public static final String FUNC_ITEM_TYPE = "get_item_type";
    public static final String FUNC_ITEM_ID = "get_item_id";
    public static final String FUNC_COMPONENTS = "get_components";
    public static final String FUNC_COMPONENTS_SET = "set_components";

    public ItemStackScriptObject() {
        super("An item stack.", doc -> {
            doc.addFunction(FUNC_COUNT, "Gets the number of items in this stack.", args -> {}, Argtypes.INTEGER);
            doc.addFunction(FUNC_COUNT_SET, "Sets the number of items in this stack.", args -> {
                args.add("count", Argtypes.INTEGER, "Count to set.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_ITEM_TYPE, "Gets the item type of this stack.", args -> {}, ScriptObjects.ITEM);
            doc.addFunction(FUNC_ITEM_ID, "Gets the item id of this stack.", args -> {}, Argtypes.STRING);
        });
    }

    @Override
    public void toTable(ItemStack obj, LuaTableBuilder builder, LuaScript script) {
        int ptr = script.addUnserializableData(obj);
        builder.add(PROP_UNSERIALIZABLE_POINTER, ptr);

        builder.add(FUNC_COUNT, args -> LuaValue.valueOf(obj.getCount()));
        builder.add(FUNC_COUNT_SET, args -> {
            obj.setCount(args.arg1().toint());
            return LuaValue.NIL;
        });
        builder.add(FUNC_ITEM_TYPE, args -> LuaTableBuilder.provide(b -> ScriptObjects.ITEM.toTable(obj.getItem(), b, script)));
        builder.add(FUNC_ITEM_ID, args -> LuaValue.valueOf(BuiltInRegistries.ITEM.getId(obj.getItem())));

    }

    @Override
    public ItemStack toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        int ptr = table.get(PROP_UNSERIALIZABLE_POINTER).toint();
        return script.getUnserializableData(ptr, ItemStack.class);
    }

    @Override
    public String getArgtypeString() {
        return "ItemStack";
    }
}
