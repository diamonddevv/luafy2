package dev.diamond.luafy.script.api;

import dev.diamond.luafy.HelloWorldSupplier;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.resources.Identifier;
import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.MetamethodImpl;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuafyApi extends AbstractScriptApi {
    public LuafyApi(LuaScript script) {
        super("luafy", script);
    }

    @Override
    public void addFunctions(ScriptApiBuilder apiBuilder) {

        apiBuilder.addGroupless(builder -> {
            builder.add("script", args -> {
                String script = MetamethodImpl.tostring(args.arg1());
                LuaTable context = args.arg(2).isnil() ? LuaTable.tableOf() : args.arg(2).checktable();

                var future = Luafy.SCRIPT_MANAGER.get(Identifier.parse(script)).execute(this.script.getSource().getServer().createCommandSourceStack(), context);
                return LuaTableBuilder.provide(b -> ScriptObjects.SCRIPT_RESULT.toTable(future, b, this.script));
            }, "Executes the script with the given identifier, and awaits its completion. Returns future result, that can be awaited if needed.", args -> {
                args.add("script", Argtypes.STRING, "Identifier of script to be executed.");
                args.add("context", Argtypes.maybe(Argtypes.TABLE), "Context to pass to script. Defaults to an empty table.");
            }, ScriptObjects.SCRIPT_RESULT);

            builder.add("context", args -> script.getGlobals().get(LuaScript.CONTEXT_KEY),
                    "Returns the context table for this script. The contents of this table depend on the event that called it, " +
                            "or the values passed by /luafy. This is the same as the global table `ctx`.",
                    args -> {}, Argtypes.TABLE);


            builder.add("provide_hello_world", args -> {
                return LuaValue.valueOf(HelloWorldSupplier.supply(System.currentTimeMillis()));
            }, "Returns a random hello world, as the mod does when Minecraft boots.", args -> {}, Argtypes.STRING);

            builder.add("get_luaj_version", args -> {
                return LuaString.valueOf(Luafy.LUAJ_VER);
            }, "Returns the version of LuaJ used by the mod.", args -> {}, Argtypes.STRING);
        });

    }
}
