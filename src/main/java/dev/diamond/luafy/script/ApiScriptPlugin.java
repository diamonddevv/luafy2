package dev.diamond.luafy.script;

import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.autodoc.SimpleAutodocumentable;
import dev.diamond.luafy.autodoc.FunctionDocInfo;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.script.api.AbstractScriptApi;

import java.util.Objects;
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

        ScriptApiBuilder builder = new ScriptApiBuilder();
        plugin.addFunctions(builder);
        return new DocInfo(plugin.name, builder);
    }

    public String generateAutodocString() {
        StringBuilder doc = new StringBuilder();
        DocInfo info = generatePopulatedFunctionList();

        var docs = info.builder.getDocumentation();
        for (String group : docs.keySet()) {
            doc.append("--- ").append(Objects.equals(group, ScriptApiBuilder.GROUPLESS_GROUP) ? "Ungrouped Functions" : "Group " + group).append(" ---\n");
            for (var function : docs.get(group)) {
                doc.append(function.generateAutodocString());
                doc.append("\n");
            }

            doc.append("---  ---\n\n");
        }

        return doc.toString();
    }

    public record DocInfo(String name, ScriptApiBuilder builder) {}
}
