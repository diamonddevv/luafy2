package dev.diamond.luafy.script;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.registry.LuafyRegistries;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaScript {

    private final String CONTEXT_KEY = "ctx";

    private final Globals globals;
    private LuaValue script;
    private String compilationError;
    private ServerCommandSource src;

    public LuaScript(String source) {
        this.globals = new Globals();
        requireLibraries();

        try {
            this.script = this.globals.load(source);
            this.compilationError = "";
        } catch (LuaError e) {
            this.compilationError = "[LUA: COMPILATION] :: " + e.getMessage();
            Luafy.LOGGER.error(this.compilationError);
        }
    }

    public Result execute(@NotNull ServerCommandSource src) {
        return this.execute(src, LuaTable.tableOf());
    }


    public Result execute(@NotNull ServerCommandSource src, LuaTable ctx) {
        if (!compilationError.isBlank()) {
            return new Result(LuaValue.NIL, compilationError);
        }

        try {
            this.src = src;
            this.globals.set(CONTEXT_KEY, ctx);
            return new Result(this.script.call(), "");
        } catch (LuaError err) {
            String error = "[LUA: INTERPRETATION] :: " + err.getMessage();
            Luafy.LOGGER.error(error);
            return new Result(LuaValue.NIL, error);
        }
    }

    public ServerCommandSource getSource() {
        return src;
    }

    public Globals getGlobals() {
        return globals;
    }


    private void requireLibraries() {
        for (ScriptPlugin plugin : LuafyRegistries.SCRIPT_PLUGINS) {
            plugin.apply(this);
        }
    }




    public static class Result {
        private final LuaValue value;
        private final String error;

        public Result(LuaValue value, String error) {
            this.value = value;
            this.error = error;
        }

        public boolean success() {
            return error.isBlank();
        }

        public String getError() {
            return this.error;
        }

        public LuaValue getResult() {
            return this.value;
        }
    }
}
