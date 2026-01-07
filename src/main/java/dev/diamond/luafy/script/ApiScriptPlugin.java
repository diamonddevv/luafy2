package dev.diamond.luafy.script;

import dev.diamond.luafy.autodoc.SimpleAutodocumentable;
import dev.diamond.luafy.autodoc.FunctionDocInfo;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.script.api.AbstractScriptApi;

import java.util.function.Function;

public class ApiScriptPlugin<T extends AbstractScriptApi> extends ScriptPlugin implements SimpleAutodocumentable {
    private final Function<LuaScript,T> constructor;

    public ApiScriptPlugin(Function<LuaScript, T> constructor) {
        super(s -> s.getGlobals().load(constructor.apply(s)));
        this.constructor = constructor;
    }

    public DocInfo generatePopulatedFunctionList() {
        LuaScript autodocScript = new LuaScript("return");
        T plugin = this.constructor.apply(autodocScript);

        FunctionListBuilder builder = new FunctionListBuilder();
        plugin.addFunctions(builder);
        return new DocInfo(plugin.name, builder);
    }

    public String generateAutodocString() {
        StringBuilder doc = new StringBuilder();
        DocInfo info = generatePopulatedFunctionList();

        for (FunctionDocInfo function : info.builder.getDocumentation()) {
            doc.append(function.generateAutodocString());
            doc.append("\n");
        }

        return doc.toString();
    }

    public record DocInfo(String name, FunctionListBuilder builder) {}
}
