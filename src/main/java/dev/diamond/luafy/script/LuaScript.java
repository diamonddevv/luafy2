package dev.diamond.luafy.script;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.api.MinecraftApi;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.*;

public class LuaScript {


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
        if (!compilationError.isBlank()) {
            return new Result(LuaValue.NIL, compilationError);
        }

        try {
            this.src = src;
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



    private void requireLibraries() {

        // luaj
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new JseStringLib());
        globals.load(new CoroutineLib());
        globals.load(new JseMathLib());
        globals.load(new JseIoLib());
        globals.load(new JseOsLib());
        globals.load(new LuajavaLib());
        LuaC.install(globals);
        LoadState.install(globals);

        // luafy
        globals.load(new MinecraftApi(this));
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
