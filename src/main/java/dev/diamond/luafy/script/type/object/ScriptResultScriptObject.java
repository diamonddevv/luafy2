package dev.diamond.luafy.script.type.object;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.lua.MetamethodNames;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.ScriptExecutionResult;
import net.minecraft.commands.CommandBuildContext;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.minecraft.commands.CommandSourceStack;

public class ScriptResultScriptObject extends AbstractScriptObject<Future<ScriptExecutionResult>> {

    private static final String PROP_INDEX = "_idx";
    private static final String FUNC_AWAIT_RESULT = "await_result";
    private static final String FUNC_AWAIT_SUCCESS = "await_success";
    private static final String FUNC_AWAIT_ERROR = "await_error";
    private static final String FUNC_RELEASE = "release";

    public ScriptResultScriptObject() {
        super("Object representing the potential result of a script execution. " +
                "Since scripts run asynchronously, this object allows for a result to be awaited if needed.", doc -> {

            doc.addFunction(FUNC_AWAIT_RESULT, "Awaits this script to complete execution if it has not already, and returns the result.",
                    args -> {}, Argtypes.maybe(Argtypes.VALUE));
            doc.addFunction(FUNC_AWAIT_SUCCESS, "Awaits this script to complete execution if it has not already, and returns if it succeeded.",
                    args -> {}, Argtypes.BOOLEAN);
            doc.addFunction(FUNC_AWAIT_ERROR, "Awaits this script to complete execution if it has not already, and returns the error string if it failed, or nil if it succeeded.",
                    args -> {}, Argtypes.maybe(Argtypes.STRING));
            doc.addFunction(FUNC_RELEASE, "Releases the internal Result Java object from the cache. Using this object after this has been called may result in an error.",
                    args -> {}, Argtypes.NIL);
        });
    }

    @Override
    public void toTable(Future<ScriptExecutionResult> obj, LuaTableBuilder builder, LuaScript script) {
        int idx = script.addUnserializableData(obj);

        builder.add(PROP_INDEX, idx);
        builder.add(FUNC_AWAIT_RESULT, args -> {
            try {
                return obj.get().getResult();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        builder.add(FUNC_AWAIT_SUCCESS, args -> {
            try {
                return LuaValue.valueOf(obj.get().success());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        builder.add(FUNC_AWAIT_ERROR, args -> {
            try {
                ScriptExecutionResult result = obj.get();
                return result.success() ? LuaValue.NIL : LuaValue.valueOf(result.getError());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        builder.add(FUNC_RELEASE, args -> {
            script.releaseUnserializableData(idx);
            return LuaValue.NIL;
        });

        builder.addMetamethod(MetamethodNames.TO_STRING, args -> {
            try {
                return LuaValue.valueOf(MetamethodImpl.tostring(obj.get().getResult()));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Future<ScriptExecutionResult> toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        // technically, this probably isn't needed. i doubt variables of this type would ever be passed around, but just in case, i guess.
        int idx = table.get(PROP_INDEX).toint();
        return script.getUnserializableData(idx, DummyFutureLuaScriptResultWrapper.class);
    }

    @Override
    public String getArgtypeString() {
        return "ScriptResult";
    }

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.empty();
    }

    @Override
    public Optional<LuaTable> parseCommand(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        return Optional.empty();
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.empty();
    }

    private static abstract class DummyFutureLuaScriptResultWrapper implements Future<ScriptExecutionResult> {}
}
