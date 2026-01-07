package dev.diamond.luafy.lua;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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

    public static LuaTable fromNbtCompound(NbtCompound compound) {
        LuaTableBuilder builder = new LuaTableBuilder();

        for (var key : compound.getKeys()) {
            NbtElement element = compound.get(key);
            assert element != null;
            builder.addInternal(key, fromNbtElement(element));
        }

        return builder.build();
    }

    private static LuaValue fromNbtElement(NbtElement element) {
        return switch (element.getType()) {
            case NbtElement.BYTE_TYPE -> LuaValue.valueOf(element.asByte().orElseThrow());
            case NbtElement.SHORT_TYPE -> LuaValue.valueOf(element.asShort().orElseThrow());
            case NbtElement.INT_TYPE -> LuaValue.valueOf(element.asInt().orElseThrow());
            case NbtElement.LONG_TYPE -> LuaValue.valueOf(element.asLong().orElseThrow());
            case NbtElement.FLOAT_TYPE -> LuaValue.valueOf(element.asFloat().orElseThrow());
            case NbtElement.DOUBLE_TYPE -> LuaValue.valueOf(element.asDouble().orElseThrow());
            case NbtElement.BYTE_ARRAY_TYPE -> {
                byte[] values = element.asByteArray().orElseThrow();
                LuaValue[] arr = new LuaValue[values.length];
                for (int i = 0; i < values.length; i++) {
                    arr[i] = LuaValue.valueOf(values[i]);
                }
                yield LuaTable.listOf(arr);
            }
            case NbtElement.STRING_TYPE -> LuaValue.valueOf(element.asString().orElseThrow());
            case NbtElement.LIST_TYPE -> {
                NbtList list = element.asNbtList().orElseThrow();
                LuaValue[] values = new LuaValue[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    values[i] = fromNbtElement(list.get(i));
                }
                yield LuaTable.listOf(values);
            }
            case NbtElement.COMPOUND_TYPE -> fromNbtCompound(element.asCompound().orElseThrow());
            case NbtElement.INT_ARRAY_TYPE -> {
                int[] values = element.asIntArray().orElseThrow();
                LuaValue[] arr = new LuaValue[values.length];
                for (int i = 0; i < values.length; i++) {
                    arr[i] = LuaValue.valueOf(values[i]);
                }
                yield LuaTable.listOf(arr);
            }
            case NbtElement.LONG_ARRAY_TYPE -> {
                long[] values = element.asLongArray().orElseThrow();
                LuaValue[] arr = new LuaValue[values.length];
                for (int i = 0; i < values.length; i++) {
                    arr[i] = LuaValue.valueOf(values[i]);
                }
                yield LuaTable.listOf(arr);
            }
            default -> LuaValue.NIL;
        };
    }
}
