package dev.diamond.luafy.lua;

import com.mojang.datafixers.util.Either;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.enumeration.ScriptEnum;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.*;

import java.util.ArrayList;
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

        private <T> T optionalVal(LuaValue val, T def, Function<LuaValue, T> func) {
            if (val.isnil()) return def;
            else return func.apply(val);
        }


        public LuaTable getTable(LuaValue val) {
            return val.checktable();
        }

        public int getInt(LuaValue val) {
            return val.checkint();
        }

        public float getFloat(LuaValue val) {
            return (float) val.checkdouble();
        }

        public boolean getBoolean(LuaValue val) {
            return val.checkboolean();
        }

        public String getString(LuaValue val) {
            return MetamethodImpl.tostring(val);
        }

        public LuaValue getLuaValue(LuaValue val) {
            return val;
        }

        public LuaFunction getFunction(LuaValue val) {
            return val.checkfunction();
        }

        public <T extends Enum<T>> T getEnumKey(ScriptEnum<T> scriptEnum, LuaValue val) {
            return scriptEnum.fromKey(getString(val));
        }

        public <T> T getScriptObject(AbstractScriptObject<T> obj, LuaValue val, CommandSourceStack src, LuaScript script) {
            return obj.toThing(val.checktable(), src, script);
        }

        public <T> ArrayList<T> getArray(LuaValue val, Function<LuaValue, T> getter) {
            LuaTable table = getTable(val);
            ArrayList<T> ts = new ArrayList<>();

            for (LuaValue key : table.keys()) {
                var luaValue = table.get(key);
                ts.add(getter.apply(luaValue));
            }

            return ts;
        }

        // nexts, no def

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

        public <T extends Enum<T>> T nextEnumKey(ScriptEnum<T> scriptEnum) {
            return getEnumKey(scriptEnum, next());
        }

        public <T> T nextScriptObject(AbstractScriptObject<T> obj, CommandSourceStack src, LuaScript script) {
            return getScriptObject(obj, next(), src, script);
        }

        public <T> ArrayList<T> nextArray(Function<LuaValue, T> getter) {
            return getArray(next(), getter);
        }

        // nexts with defs

        public LuaTable nextTable(LuaTable def) {
            return optionalVal(next(), def, this::getTable);
        }

        public int nextInt(int def) {
            return optionalVal(next(), def, this::getInt);
        }

        public float nextFloat(float def) {
            return optionalVal(next(), def, this::getFloat);
        }

        public boolean nextBoolean(boolean def) {
            return optionalVal(next(), def, this::getBoolean);
        }

        public String nextString(String def) {
            return optionalVal(next(), def, this::getString);
        }

        public LuaValue nextLuaValue(LuaValue def) {
            return optionalVal(next(), def, this::getLuaValue);
        }

        public LuaFunction nextFunction(LuaFunction def) {
            return optionalVal(next(), def, this::getFunction);
        }

        public <T extends Enum<T>> T nextEnumKey(ScriptEnum<T> scriptEnum, T def) {
            return optionalVal(next(), def, v -> getEnumKey(scriptEnum, v));
        }

        public <T> T nextScriptObject(AbstractScriptObject<T> obj, CommandSourceStack src, LuaScript script, T def) {
            return optionalVal(next(), def, val -> getScriptObject(obj, val, src, script));
        }

        public <T> ArrayList<T> nextArray(Function<LuaValue, T> getter, ArrayList<T> def) {
            return optionalVal(next(), def, val -> getArray(val, getter));
        }



        // other stuff

        public <T, X> T map(LuaValue value, T def, Function<LuaValue, X> getter, Function<X, T> convert) {
            if (value.isnil()) {
                return def;
            } else {
                return convert.apply(getter.apply(value));
            }
        }

        public <T, X> T nextMap(T def, Function<LuaValue, X> getter, Function<X, T> convert) {
            return map(next(), def, getter, convert);
        }

    }
}
