package dev.diamond.luafy.script.object.game;

import com.mojang.serialization.DataResult;
import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


public class ItemStackScriptObject extends AbstractScriptObject<ItemStack> {
    public static final String PROP_UNSERIALIZABLE_POINTER = "_ptr";

    public static final String FUNC_ITEM_TYPE = "get_item_type";
    public static final String FUNC_ITEM_ID = "get_item_id";

    public static final String FUNC_COUNT = "get_count";
    public static final String FUNC_COUNT_SET = "set_count";

    public static final String FUNC_COMPONENT = "get_component";
    public static final String FUNC_COMPONENT_SET = "set_component";

    public ItemStackScriptObject() {
        super("An item stack.", doc -> {

            doc.addFunction(FUNC_ITEM_TYPE, "Gets the item type of this stack.", args -> {}, ScriptObjects.ITEM);
            doc.addFunction(FUNC_ITEM_ID, "Gets the item id of this stack.", args -> {}, Argtypes.STRING);

            doc.addFunction(FUNC_COUNT, "Gets the number of items in this stack.", args -> {}, Argtypes.INTEGER);
            doc.addFunction(FUNC_COUNT_SET, "Sets the number of items in this stack.", args -> {
                args.add("count", Argtypes.INTEGER, "Count to set.");
            }, Argtypes.NIL);

            doc.addFunction(FUNC_COMPONENT, "Gets a component from this stack as NBT.", args -> {
                args.add("component_id", Argtypes.STRING, "The id of the component type to fetch.");
            }, Argtypes.TABLE);
            doc.addFunction(FUNC_COMPONENT_SET, "Sets a component from this stack as NBT.", args -> {
                args.add("component_id", Argtypes.STRING, "The id of the component type.");
                args.add("nbt", Argtypes.TABLE, "The data to write. Will be encoded into the item stack.");
            }, Argtypes.NIL);

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

        builder.add(FUNC_COMPONENT, args -> {
            String key = MetamethodImpl.tostring(args.arg1());

            // get the component
            DataComponentType<Object> type = (DataComponentType<Object>) BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse(key)).orElseThrow().value();
            // holy cursed programming

            Object component = obj.getComponents().get(type);

            // convert component object to nbt and then to a lua table.
            // codecs are ass bro

            DataResult<Tag> result = type.codecOrThrow().encodeStart(NbtOps.INSTANCE, component);
            CompoundTag tag = result.getOrThrow().asCompound().orElse(new CompoundTag());

            return LuaTableBuilder.fromNbtCompound(tag);
        });

        builder.add(FUNC_COMPONENT_SET, args -> {
            String key = MetamethodImpl.tostring(args.arg1());
            LuaTable data = args.arg(2).checktable();

            // get the component
            DataComponentType<Object> type = (DataComponentType<Object>) BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse(key)).orElseThrow().value();
            CompoundTag nbt = LuaTableBuilder.toNbtCompound(data);
            Object component = type.codec().decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();
            obj.set(type, component);

            return LuaValue.NIL;
        });

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
