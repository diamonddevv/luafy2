package dev.diamond.luafy.script;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.enumeration.ScriptEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.concurrent.Future;
import net.minecraft.commands.CommandSourceStack;

public class LuaScript {

    public static final String CONTEXT_KEY = "ctx";

    private final Globals globals;
    private LuaValue script;
    private String compilationError;
    private CommandSourceStack src;
    private final HashMap<Integer, Object> unserializableDataReferences;
    private int nextUnserializableDataReferenceIndex;

    public LuaScript(String source) {
        this.globals = new Globals();
        injectSources();

        this.unserializableDataReferences = new HashMap<>();
        this.nextUnserializableDataReferenceIndex = 0;

        try {
            this.script = this.globals.load(source);
            this.compilationError = "";
        } catch (LuaError e) {
            this.compilationError = "[LUA: COMPILATION] :: " + e.getMessage();
            Luafy.LOGGER.error(this.compilationError);
        }
    }

    public int addUnserializableData(Object o) {
        unserializableDataReferences.put(nextUnserializableDataReferenceIndex, o);
        return nextUnserializableDataReferenceIndex++;
    }

    public <T> T getUnserializableData(int idx, Class<T> clazz) {
        return clazz.cast(unserializableDataReferences.get(idx));
    }

    public void releaseUnserializableData(int idx) {
        unserializableDataReferences.remove(idx);
    }

    public Future<ScriptExecutionResult> execute(@NotNull CommandSourceStack src) {
        return this.execute(src, LuaTable.tableOf());
    }

    public Future<ScriptExecutionResult> execute(@NotNull CommandSourceStack src, @Nullable LuaTable ctx) {
        return Luafy.SCRIPT_MANAGER.submitExecution(() -> this.executor(src, ctx));
    }

    public CommandSourceStack getSource() {
        return src;
    }

    public Globals getGlobals() {
        return globals;
    }


    private void injectSources() {
        for (ScriptPlugin plugin : LuafyRegistries.SCRIPT_PLUGINS) {
            plugin.apply(this);
        }

        for (ScriptEnum<?> e : LuafyRegistries.SCRIPT_ENUMS) {
            this.globals.set(e.getArgtypeString(), LuaTableBuilder.provide(b -> {
                for (var key : e.getEnumKeys()) b.add(key, key);
            }));
        }
    }

    private ScriptExecutionResult executor(@NotNull CommandSourceStack src, @Nullable LuaTable ctx) {
        if (!compilationError.isBlank()) {
            return new ScriptExecutionResult(LuaValue.NIL, compilationError);
        }
        try {
            this.src = src.withSuppressedOutput();
            if (ctx == null) {
                ctx = LuaTable.tableOf();
            }
            this.globals.set(CONTEXT_KEY, ctx);
            return new ScriptExecutionResult(this.script.call(), "");
        } catch (LuaError err) {
            String error = "[LUA: INTERPRETATION] :: " + err.getMessage();
            Luafy.LOGGER.error(error);
            return new ScriptExecutionResult(LuaValue.NIL, error);
        }
    }


}
