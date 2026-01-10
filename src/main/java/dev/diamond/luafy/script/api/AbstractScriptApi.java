package dev.diamond.luafy.script.api;

import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.script.LuaScript;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public abstract class AbstractScriptApi extends TwoArgFunction {
    public final String name;
    protected final LuaScript script;

    public AbstractScriptApi(String name, LuaScript script) {
        this.name = name;
        this.script = script;
    }

    public abstract void addFunctions(ScriptApiBuilder builder);

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {

        LuaTable functions = new LuaTable();

        ScriptApiBuilder builder = new ScriptApiBuilder();
        this.addFunctions(builder);
        builder.build(functions);

        env.set(name, functions);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set(name, functions);
        }

        return functions;
    }

}
