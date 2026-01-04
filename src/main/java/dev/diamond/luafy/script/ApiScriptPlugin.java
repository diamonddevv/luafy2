package dev.diamond.luafy.script;

import dev.diamond.luafy.autodoc.Autodocumentable;
import dev.diamond.luafy.autodoc.FunctionDocInfo;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.script.api.AbstractScriptApi;

import java.util.function.Function;

public class ApiScriptPlugin<T extends AbstractScriptApi> extends ScriptPlugin implements Autodocumentable {
    private final Function<LuaScript,T> constructor;

    public ApiScriptPlugin(Function<LuaScript, T> constructor) {
        super(s -> s.getGlobals().load(constructor.apply(s)));
        this.constructor = constructor;
    }

    public String generateAutodoc() {
        LuaScript autodocScript = new LuaScript("return");
        T plugin = this.constructor.apply(autodocScript);

        StringBuilder doc = new StringBuilder();
        FunctionListBuilder builder = new FunctionListBuilder();
        plugin.addFunctions(builder);

        for (FunctionDocInfo function : builder.getDocumentation()) {
            doc.append(function.generateAutodoc());
            doc.append("\n");
        }

        return doc.toString();
    }
}
