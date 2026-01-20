package dev.diamond.luafy.script.object;

import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodNames;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


public class NbtTableScriptObject extends AbstractScriptObject<CompoundTag> {
    public static final String PROP_TABLE = "_table";

    public NbtTableScriptObject() {
        super("NBT Compound Tag", args -> {

        });
    }

    @Override
    public void toTable(CompoundTag obj, LuaTableBuilder builder, LuaScript script) {
        LuaTable table = LuaTableBuilder.fromNbtCompound(obj);

        builder.add(PROP_TABLE, table);

        builder.addMetamethod(MetamethodNames.TO_STRING, args -> LuaString.valueOf(obj.toString()));
    }

    @Override
    public CompoundTag toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        LuaTable backing = table.get(PROP_TABLE).checktable();
        return LuaTableBuilder.toNbtCompound(backing);
    }

    @Override
    public String getArgtypeString() {
        return "NbtTable";
    }
}
