package dev.diamond.luafy.autodoc;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class FunctionListBuilder {
    private final HashMap<String, LuaFunction> functions;
    private final Collection<FunctionDocInfo> documentation;

    public FunctionListBuilder() {
        this.functions = new HashMap<>();
        this.documentation = new ArrayList<>();
    }

    public void add(String name, Function<Varargs, LuaValue> function, String desc, Consumer<ArglistBuilder> arglistBuilder, Argtype returnType) {
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

    public void build(LuaTable table) {
        for (var pair : functions.entrySet()) {
            table.set(pair.getKey(), pair.getValue());
        }
    }

    public Collection<FunctionDocInfo> getDocumentation() {
        return documentation;
    }
}
