package dev.diamond.luafy.lua;

import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.*;

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
