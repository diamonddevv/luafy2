package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.LuaScript;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
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
        private final Collection<FunctionDocInfo> documentation;

        public FunctionListBuilder() {
            this.functions = new HashMap<>();
            this.documentation = new ArrayList<>();
        }

        public void add(String name, Function<Varargs, LuaValue> function, String desc, Consumer<ArglistBuilder> arglistBuilder, String returnType) {
            this.functions.put(name, new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    return function.apply(args);
                }
            });

            ArglistBuilder builder = new ArglistBuilder();
            arglistBuilder.accept(builder);
            this.documentation.add(new FunctionDocInfo(name, desc, builder.args, returnType));
        }

        private void build(LuaTable table) {
            for (var pair : functions.entrySet()) {
                table.set(pair.getKey(), pair.getValue());
            }
        }

        public Collection<FunctionDocInfo> getDocumentation() {
            return documentation;
        }
    }

    public record FunctionDocInfo(String funcName, String funcDesc, ArrayList<ArgDocInfo> args, String returnType) {}
    public record ArgDocInfo(String argName, String argType, String argDesc) { }

    public static class ArglistBuilder {
        private final ArrayList<ArgDocInfo> args;

        public ArglistBuilder() {
            this.args = new ArrayList<>();
        }

        public void add(String argName, String argType, String argDesc) {
            this.args.add(new ArgDocInfo(argName, argType, argDesc));
        }
    }
}
