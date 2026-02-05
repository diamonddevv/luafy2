package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.script.LuaScript;
import org.luaj.vm2.LuaValue;

public class WebApi extends AbstractScriptApi {
    public WebApi(LuaScript script) {
        super("web", script);
    }

    @Override
    public void addFunctions(ScriptApiBuilder api) {
        api.addGroupless(builder -> {
            builder.add("download", args -> {
                String url = args.nextString();

                return LuaValue.valueOf("");
            }, "Downloads content from the specified URL. Blocks until the request is complete. Returns content as a String", args -> {
                args.add("url", Argtypes.STRING, "URL to make request from.");
            }, Argtypes.STRING);
        });
    }
}
