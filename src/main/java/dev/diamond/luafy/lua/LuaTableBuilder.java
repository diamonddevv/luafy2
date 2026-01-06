package dev.diamond.luafy.lua;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class LuaTableBuilder {
    private final LuaTable table;
    private final LuaTable metatable;

    public LuaTableBuilder() {
        this.table = LuaTable.tableOf();
        this.metatable = LuaTable.tableOf();
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
    public void add(String key, Function<Varargs, LuaValue> function) { addInternal(key, new VarArgFunction() {
        @Override
        public Varargs invoke(Varargs args) {
            return function.apply(args);
        }
    }); }
    public void addMetamethod(LuaString key, Function<Varargs, LuaValue> function) {
        this.metatable.set(key, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return function.apply(args);
            }
        });
    }


    public LuaTable build() {
        this.table.setmetatable(this.metatable);
        return this.table;
    }

    private static LuaTable ofArray(LuaValue[] values) {
        return LuaTable.listOf(values);
    }

    public static LuaTable ofArrayInts(ArrayList<Integer> ints)         { return ofArray((LuaValue[]) ints      .stream().map(LuaValue::valueOf).toArray()); }
    public static LuaTable ofArrayStrings(ArrayList<String> strings)    { return ofArray((LuaValue[]) strings   .stream().map(LuaValue::valueOf).toArray()); }
    public static LuaTable ofArrayBools(ArrayList<Boolean> bools)       { return ofArray((LuaValue[]) bools     .stream().map(LuaValue::valueOf).toArray()); }
    public static LuaTable ofArrayFloats(ArrayList<Float> floats)       { return ofArray((LuaValue[]) floats    .stream().map(LuaValue::valueOf).toArray()); }


    public static LuaTable provide(Consumer<LuaTableBuilder> table) {
        var b = new LuaTableBuilder();
        table.accept(b);
        return b.build();
    }
}
