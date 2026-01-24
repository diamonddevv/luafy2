package dev.diamond.luafy.lua;

import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.*;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface ScriptFunction extends Function<Varargs, LuaValue> {

    LuaValue call(ArgumentSupplier args);

    @Override
    default LuaValue apply(Varargs args) {
        return call(new ArgumentSupplier(args));
    }

    class ArgumentSupplier {
        private final Varargs args;
        private int argIdx;

        private ArgumentSupplier(Varargs args) {
            this.args = args;
            this.argIdx = 1;
        }

        private LuaValue idx(int idx) {
            return args.arg(idx);
        }
        private LuaValue next() {
            return idx(this.argIdx++);
        }



        public LuaTable getTable(LuaValue val, LuaTable def) {
            return val.opttable(def);
        }

        public int getInt(LuaValue val, int def) {
            return val.optint(def);
        }

        public float getFloat(LuaValue val, float def) {
            return (float) val.optdouble(def);
        }

        public boolean getBoolean(LuaValue val, boolean def) {
            return val.optboolean(def);
        }

        public String getString(LuaValue val, String def) {
            return MetamethodImpl.tostring(val.optstring(LuaString.valueOf(def)));
        }

        public LuaValue getLuaValue(LuaValue val, LuaValue def) {
            return val.optvalue(def);
        }

        public LuaFunction getFunction(LuaValue val, LuaFunction def) {
            return val.optfunction(def);
        }

        public <T> T getScriptObject(AbstractScriptObject<T> obj, LuaValue val, CommandSourceStack src, LuaScript script, T def) {
            LuaTable table = val.checktable();
            if (table.isnil()) {
                return def;
            } else {
                return obj.toThing(table, src, script);
            }
        }

        public <T> ArrayList<T> getArray(int idx, BiFunction<LuaValue, T, T> getter) {
            LuaTable table = getTable(idx);
            ArrayList<T> ts = new ArrayList<>();

            for (LuaValue key : table.keys()) {
                var luaValue = table.get(key);
            }

        }


        public LuaTable nextTable() {
            return getTable(next());
        }

        public int nextInt() {
            return getInt(next());
        }

        public float nextFloat() {
            return getFloat(next());
        }

        public boolean nextBoolean() {
            return getBoolean(next());
        }

        public String nextString() {
            return getString(next());
        }

        public LuaValue nextLuaValue() {
            return getLuaValue(next());
        }

        public LuaFunction nextFunction() {
            return getFunction(next());
        }

        public <T> T nextScriptObject(AbstractScriptObject<T> obj, CommandSourceStack src, LuaScript script) {
            return getScriptObject(obj, next(), src, script);
        }

        // defaults

        public LuaTable nextTable(LuaTable def) {
            return getTable(next(), def);
        }

        public int nextInt(int def) {
            return getInt(next(), def);
        }

        public float nextFloat(float def) {
            return getFloat(next(), def);
        }

        public boolean nextBoolean(boolean def) {
            return getBoolean(next(), def);
        }

        public String nextString(String def) {
            return getString(next(), def);
        }

        public LuaValue nextLuaValue(LuaValue def) {
            return getLuaValue(next(), def);
        }

        public LuaFunction nextFunction(LuaFunction def) {
            return getFunction(next(), def);
        }

        public <T> T nextScriptObject(AbstractScriptObject<T> obj, CommandSourceStack src, LuaScript script, T def) {
            return getScriptObject(obj, next(), src, script, def);
        }

    }
}
