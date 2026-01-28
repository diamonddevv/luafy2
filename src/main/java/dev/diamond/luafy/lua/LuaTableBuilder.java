package dev.diamond.luafy.lua;

import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.nbt.*;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
    public void add(String key, ScriptFunction function) { addInternal(key, new VarArgFunction() {
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

    public LuaTable futureSelf() {
        if (this.table.isnil()) {
            throw new RuntimeException("Tried to return self before it exists!");
        }
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
    public static LuaTable ofArrayTables(Collection<LuaTable> tables)    { return ofArray(tables    .stream()                       .toList()); }

    public static LuaTable provide(Consumer<LuaTableBuilder> table) {
        var b = new LuaTableBuilder();
        table.accept(b);
        return b.build();
    }

    public static <T> LuaTable provide(AbstractScriptObject<T> obj, T t, LuaScript script) {
        return provide(b -> obj.toTable(t, b, script));
    }

    public static LuaTable fromNbtCompound(CompoundTag compound) {
        LuaTableBuilder builder = new LuaTableBuilder();

        for (var key : compound.keySet()) {
            Tag element = compound.get(key);
            assert element != null;
            builder.addInternal(key, fromNbt(element));
        }

        return builder.build();
    }

    private static LuaValue fromNbt(Tag element) {
        return switch (element.getId()) {
            case Tag.TAG_BYTE -> LuaValue.valueOf(element.asByte().orElseThrow());
            case Tag.TAG_SHORT -> LuaValue.valueOf(element.asShort().orElseThrow());
            case Tag.TAG_INT -> LuaValue.valueOf(element.asInt().orElseThrow());
            case Tag.TAG_LONG -> LuaValue.valueOf(element.asLong().orElseThrow());
            case Tag.TAG_FLOAT -> LuaValue.valueOf(element.asFloat().orElseThrow());
            case Tag.TAG_DOUBLE -> LuaValue.valueOf(element.asDouble().orElseThrow());
            case Tag.TAG_BYTE_ARRAY -> {
                byte[] values = element.asByteArray().orElseThrow();
                LuaValue[] arr = new LuaValue[values.length];
                for (int i = 0; i < values.length; i++) {
                    arr[i] = LuaValue.valueOf(values[i]);
                }
                yield LuaTable.listOf(arr);
            }
            case Tag.TAG_STRING -> LuaValue.valueOf(element.asString().orElseThrow());
            case Tag.TAG_LIST -> {
                ListTag list = element.asList().orElseThrow();
                LuaValue[] values = new LuaValue[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    values[i] = fromNbt(list.get(i));
                }
                yield LuaTable.listOf(values);
            }
            case Tag.TAG_COMPOUND -> fromNbtCompound(element.asCompound().orElseThrow());
            case Tag.TAG_INT_ARRAY -> {
                int[] values = element.asIntArray().orElseThrow();
                LuaValue[] arr = new LuaValue[values.length];
                for (int i = 0; i < values.length; i++) {
                    arr[i] = LuaValue.valueOf(values[i]);
                }
                yield LuaTable.listOf(arr);
            }
            case Tag.TAG_LONG_ARRAY -> {
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

    public static CompoundTag toNbtCompound(LuaTable table) {
        CompoundTag nbt = new CompoundTag();
        for (LuaValue key : table.keys()) {
            toNbt(table.get(key)).ifPresent(value -> nbt.put(MetamethodImpl.tostring(key), value));
        }
        return nbt;
    }

    private static Optional<Tag> toNbt(LuaValue value) {

        return switch (value.type()) {
            case LuaValue.TINT ->       Optional.of(IntTag.valueOf(value.toint()));
            case LuaValue.TNUMBER ->    Optional.of(FloatTag.valueOf(value.tofloat()));
            case LuaValue.TBOOLEAN ->   Optional.of(ByteTag.valueOf(value.toboolean()));
            case LuaValue.TSTRING ->    Optional.of(StringTag.valueOf(MetamethodImpl.tostring(value)));
            case LuaValue.TTABLE ->     {
                LuaTable table = value.checktable();

                if (Arrays.stream(table.keys()).allMatch(LuaValue::isint)) {
                    ListTag list = new ListTag();
                    for (LuaValue key : table.keys()) {
                        toNbt(table.get(key)).ifPresent(list::add);
                    }
                    yield Optional.of(list);
                } else {
                    yield Optional.of(toNbtCompound(table));
                }
            }

            default -> Optional.empty();
        };
    }
}
