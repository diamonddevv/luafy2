package dev.diamond.luafy.script.api;

import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.game.TextComponentScriptObject;

public class TextApi extends AbstractScriptApi {
    public TextApi(LuaScript script) {
        super("text", script);
    }

    @Override
    public void addFunctions(ScriptApiBuilder api) {
        api.addGroupless(builder ->
                TextComponentScriptObject.addStaticTextComponentBuilderMethods(builder, script));
    }
}
