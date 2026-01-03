package dev.diamond.luafy.script;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

public class LuaTableBuilder {
    private final LuaTable table;

    public LuaTableBuilder() {
        this.table = LuaTable.tableOf();
    }

    private void addInternal(String key, LuaValue value) {
        this.table.set(key, value);
    }


    public void add(String key, int i) { addInternal(key, LuaValue.valueOf(i)); }
    public void add(String key, long l) { addInternal(key, LuaValue.valueOf(l)); }
    public void add(String key, String s) { addInternal(key, LuaValue.valueOf(s)); }
    public void add(String key, boolean bl) { addInternal(key, LuaValue.valueOf(bl)); }
    public void add(String key, float f) { addInternal(key, LuaValue.valueOf(f)); }
    public void add(String key, double d) { addInternal(key, LuaValue.valueOf(d)); }
    public void add(String key, LuaTable tbl) { addInternal(key, tbl); }


    public LuaTable build() {
        return this.table;
    }

    private static LuaTable ofArray(LuaValue[] values) {
        return LuaTable.listOf(values);
    }

    public static LuaTable ofArrayInts(ArrayList<Integer> ints)         { return ofArray((LuaValue[]) ints      .stream().map(LuaValue::valueOf).toArray()); }
    public static LuaTable ofArrayStrings(ArrayList<String> strings)    { return ofArray((LuaValue[]) strings   .stream().map(LuaValue::valueOf).toArray()); }
    public static LuaTable ofArrayBools(ArrayList<Boolean> bools)       { return ofArray((LuaValue[]) bools     .stream().map(LuaValue::valueOf).toArray()); }
    public static LuaTable ofArrayFloats(ArrayList<Float> floats)       { return ofArray((LuaValue[]) floats    .stream().map(LuaValue::valueOf).toArray()); }
}
