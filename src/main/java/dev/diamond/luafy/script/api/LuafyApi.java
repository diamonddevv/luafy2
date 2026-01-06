package dev.diamond.luafy.script.api;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.autodoc.ArgtypeStrings;
import dev.diamond.luafy.lua.MetamethodImpl;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaValue;

public class LuafyApi extends AbstractScriptApi {
    public LuafyApi(LuaScript script) {
        super("luafy", script);
    }

    @Override
    public void addFunctions(FunctionListBuilder builder) {
        builder.add("script", args -> {
            String script = MetamethodImpl.tostring(args.arg1());
            var result = Luafy.SCRIPT_MANAGER.get(Identifier.of(script)).execute(this.script.getSource().getServer().getCommandSource());
            if (result.success()) {
                return result.getResult();
            }
            return LuaValue.NIL;
        }, "Executes the script with the given identifier. Returns the value returned from this script.", args -> {
            args.add("script", ArgtypeStrings.STRING, "Identifier of script to be executed.");
        }, ArgtypeStrings.VALUE);
    }
}
