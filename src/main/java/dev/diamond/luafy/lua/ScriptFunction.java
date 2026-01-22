package dev.diamond.luafy.lua;

import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.*;

import java.lang.reflect.Array;
import java.util.function.Function;

@FunctionalInterface
public interface ScriptFunction extends Function<Varargs, LuaValue> {

    Object call(ArgumentSupplier args);

    @Override
    default LuaValue apply(Varargs args) {
        Object obj = call(new ArgumentSupplier(args));
        return ScriptFunction.adapt(obj);
    }

    static LuaValue adapt(Object obj) {

        if (obj instanceof LuaValue val) {
            return val;

        } else if (obj == null) {
            return LuaValue.NIL;
        } else if (obj instanceof Integer i) { // primitives + string
            return LuaValue.valueOf(i);
        } else if (obj instanceof Byte b) {
            return LuaValue.valueOf(b);
        } else if (obj instanceof Long l) {
            return LuaValue.valueOf(l);
        } else if (obj instanceof Float f) {
            return LuaValue.valueOf(f);
        } else if (obj instanceof Double d) {
            return LuaValue.valueOf(d);
        } else if (obj instanceof Boolean bl) {
            return LuaValue.valueOf(bl);
        } else if (obj instanceof String s) {
            return LuaValue.valueOf(s);
        } else if (obj instanceof Short sh) {
            return LuaValue.valueOf(sh);
        } else if (obj.getClass().isArray()) { // should probably be avoided anyway
            Object[] arr = (Object[]) obj;
            LuaValue[] luaArr = new LuaValue[arr.length];
            for (int i = 0; i < arr.length; i++) luaArr[i] = adapt(arr[i]);
            return LuaTable.tableOf(luaArr);
        } else { // script objects

            for (var asco : LuafyRegistries.SCRIPT_OBJECTS) {
                if (obj.getClass() == asco.getType()) {
                    return LuaTableBuilder.provide(asco, obj, script);
                }
            }

        }


        throw new RuntimeException("Couldn't adapt some JVM value to a value returnable to Lua " +
                "(It might not be registered)");
    }

    class ArgumentSupplier {
        private final Varargs args;
        private int argIdx;

        private ArgumentSupplier(Varargs args) {
            this.args = args;
            this.argIdx = 1;
        }

        public LuaTable getTable(int idx) {
            return args.arg(idx).checktable();
        }

        public int getInt(int idx) {
            return args.arg(idx).toint();
        }

        public float getFloat(int idx) {
            return args.arg(idx).tofloat();
        }

        public boolean getBoolean(int idx) {
            return args.arg(idx).toboolean();
        }

        public String getString(int idx) {
            return MetamethodImpl.tostring(args.arg(idx));
        }

        public LuaValue getLuaValue(int idx) {
            return args.arg(idx);
        }

        public LuaFunction getFunction(int idx) {
            return args.arg(idx).checkfunction();
        }

        public <T> T getScriptObject(AbstractScriptObject<T> obj, int idx, CommandSourceStack src, LuaScript script) {
            return obj.toThing(getTable(idx), src, script);
        }


        public LuaTable nextTable() {
            return args.arg(this.argIdx++).checktable();
        }

        public int nextInt() {
            return args.arg(this.argIdx++).toint();
        }

        public float nextFloat() {
            return args.arg(this.argIdx++).tofloat();
        }

        public boolean nextBoolean() {
            return args.arg(this.argIdx++).toboolean();
        }

        public String nextString() {
            return MetamethodImpl.tostring(args.arg(this.argIdx++));
        }

        public LuaValue nextLuaValue() {
            return args.arg(this.argIdx++);
        }

        public LuaFunction nextFunction() {
            return args.arg(this.argIdx++).checkfunction();
        }

        public <T> T nextScriptObject(AbstractScriptObject<T> obj, CommandSourceStack src, LuaScript script) {
            return obj.toThing(getTable(this.argIdx++), src, script);
        }


        public LuaTable nextTable(LuaTable def) {
            return args.arg(this.argIdx++).checktable().opttable(def);
        }

        public int nextInt(int def) {
            return args.arg(this.argIdx++).optint(def);
        }

        public float nextFloat(float def) {
            return (float) args.arg(this.argIdx++).optdouble(def);
        }

        public boolean nextBoolean(boolean def) {
            return args.arg(this.argIdx++).optboolean(def);
        }

        public String nextString(String def) {
            return MetamethodImpl.tostring(args.arg(this.argIdx++).optstring(LuaString.valueOf(def)));
        }

        public LuaValue nextLuaValue(LuaValue def) {
            return args.arg(this.argIdx++).optvalue(def);
        }

        public LuaFunction nextFunction(LuaFunction def) {
            return args.arg(this.argIdx++).optfunction(def);
        }

        public <T> T nextScriptObject(AbstractScriptObject<T> obj, CommandSourceStack src, LuaScript script, T def) {
            LuaTable table = getTable(this.argIdx++);
            if (table.isnil()) {
                return def;
            } else {
                return obj.toThing(table, src, script);
            }
        }

    }
}
