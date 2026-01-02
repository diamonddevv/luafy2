package dev.diamond.luafy.script;

import dev.diamond.luafy.Autodocumentable;
import dev.diamond.luafy.script.api.AbstractScriptApi;

import java.util.function.Function;

public class ApiScriptPlugin<T extends AbstractScriptApi> extends ScriptPlugin implements Autodocumentable {
    public static final String TAB = "    ";

    private final Function<LuaScript,T> constructor;

    public ApiScriptPlugin(Function<LuaScript, T> constructor) {
        super(s -> s.getGlobals().load(constructor.apply(s)));
        this.constructor = constructor;
    }

    public String generateAutodoc() {
        LuaScript autodocScript = new LuaScript("return");
        T plugin = this.constructor.apply(autodocScript);

        StringBuilder doc = new StringBuilder();
        AbstractScriptApi.FunctionListBuilder builder = new AbstractScriptApi.FunctionListBuilder();
        plugin.addFunctions(builder);

        for (AbstractScriptApi.FunctionDocInfo function : builder.getDocumentation()) {
            doc.append(function.funcName());
            doc.append("(");
            for (int i = 0; i < function.args().size(); i++) {
                var arg = function.args().get(i);
                doc.append(arg.argName());
                doc.append(": ");
                doc.append(arg.argType());
                if (i < function.args().size() - 1) {
                    doc.append(", ");
                }
            }
            doc.append("): ");
            doc.append(function.returnType());
            doc.append("\n");

            doc.append(TAB);
            doc.append(function.funcDesc());
            doc.append("\n");
            for (var arg : function.args()) {
                doc.append(TAB + "- ");
                doc.append(arg.argName());
                doc.append(" : ");
                doc.append(arg.argDesc());
                doc.append("\n");
            }
            doc.append("\n");
        }

        return doc.toString();
    }
}
