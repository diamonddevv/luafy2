package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.LuaScript;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;
import java.util.function.Function;

public abstract class AbstractScriptApi extends TwoArgFunction {
    private final String name;
    protected final LuaScript script;

    public AbstractScriptApi(String name, LuaScript script) {
        this.name = name;
        this.script = script;
    }

    public abstract void addFunctions(FunctionListBuilder builder);

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {

        LuaTable functions = new LuaTable();

        FunctionListBuilder builder = new FunctionListBuilder();
        this.addFunctions(builder);
        builder.build(functions);

        env.set(name, functions);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set(name, functions);
        }

        return functions;
    }

    public static class FunctionListBuilder {
        private final HashMap<String, LuaFunction> functions;

        private FunctionListBuilder() {
            this.functions = new HashMap<>();
        }

        public void add(String name, Function<Varargs, LuaValue> function) {
            this.functions.put(name, new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    return function.apply(args);
                }
            });
        }

        private void build(LuaTable table) {
            for (var pair : functions.entrySet()) {
                table.set(pair.getKey(), pair.getValue());
            }
        }
    }
}
