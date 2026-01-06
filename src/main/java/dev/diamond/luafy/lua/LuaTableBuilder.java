package dev.diamond.luafy.lua;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public void addMetatag(LuaString key, LuaValue value) {
        this.metatable.set(key, value);
    }


    public LuaTable build() {
        this.table.setmetatable(this.metatable);
        return this.table;
    }

    private static <T extends LuaValue> LuaTable ofArray(List<T> values) {
        LuaValue[] arr = new LuaValue[values.size()];
        for (int i = 0; i < values.size(); i++) {
            arr[i] = values.get(i);
        }
        return LuaTable.listOf(arr);
    }

    public static LuaTable ofArrayInts(Collection<Integer> ints)         { return ofArray(ints      .stream().map(LuaValue::valueOf).toList()); }
    public static LuaTable ofArrayStrings(Collection<String> strings)    { return ofArray(strings   .stream().map(LuaValue::valueOf).toList()); }
    public static LuaTable ofArrayBools(Collection<Boolean> bools)       { return ofArray(bools     .stream().map(LuaValue::valueOf).toList()); }
    public static LuaTable ofArrayFloats(Collection<Float> floats)       { return ofArray(floats    .stream().map(LuaValue::valueOf).toList()); }


    public static LuaTable provide(Consumer<LuaTableBuilder> table) {
        var b = new LuaTableBuilder();
        table.accept(b);
        return b.build();
    }
}
