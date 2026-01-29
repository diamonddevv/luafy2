package dev.diamond.luafy.script;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.mixin.CommandSourceStackAccessor;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.resource.ScriptResourceLoader;
import dev.diamond.luafy.script.enumeration.ScriptEnum;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.Future;
import net.minecraft.commands.CommandSourceStack;

public class LuaScript {

    public static final String CONTEXT_KEY = "ctx";

    private final Globals globals;
    private final String source;
    public boolean onLibPath;
    private LuaValue script;
    private String compilationError;
    private CommandSourceStack src;
    private final HashMap<Integer, Object> unserializableDataReferences;
    private int nextUnserializableDataReferencePtr;

    public LuaScript(String source) {
        this.globals = new Globals();
        this.onLibPath = false;
        this.source = source;
        injectSources();

        this.unserializableDataReferences = new HashMap<>();
        this.nextUnserializableDataReferencePtr = 0;

        try {
            this.script = this.globals.load(source);
            this.compilationError = "";
        } catch (LuaError e) {
            this.compilationError = "[LUA: COMPILATION] :: " + e.getMessage();
            Luafy.LOGGER.error(this.compilationError);
        }
    }

    public int addUnserializableData(Object o) {
        unserializableDataReferences.put(nextUnserializableDataReferencePtr, o);
        return nextUnserializableDataReferencePtr++;
    }

    public <T> T getUnserializableData(int ptr, Class<T> clazz) {
        return clazz.cast(unserializableDataReferences.get(ptr));
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

    public <T> Registry<T> getRegistry(ResourceKey<Registry<T>> key) {
        return src.registryAccess().lookupOrThrow(key);
    }

    public <T> T lookupRegistry(ResourceKey<Registry<T>> key, Identifier id) {
        return src.registryAccess().lookupOrThrow(key).getValue(id);
    }

    public Globals getGlobals() {
        return globals;
    }

    public void setSource(@NotNull CommandSourceStack src) {
        this.src = src.withSuppressedOutput();
    }

    public void sendSourceMessage(Component c) {
        ServerPlayer serverPlayer = this.src.getPlayer();
        if (serverPlayer != null) {
            serverPlayer.sendSystemMessage(c);
        } else {
            ((CommandSourceStackAccessor)this.src).accessCommandSource().sendSystemMessage(c);
        }
    }

    private void injectSources() {
        for (ScriptPlugin plugin : LuafyRegistries.SCRIPT_PLUGINS) {
            plugin.apply(this);
        }

        modifyRequire(this);

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
            this.setSource(src);
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

    private static void modifyRequire(LuaScript script) {
        script.globals.finder = s -> {
            String namespace = ""; // todo get somehow
            Identifier id = ScriptResourceLoader.idFromBadPath(s, namespace);
            return new ByteArrayInputStream(Luafy.SCRIPT_MANAGER.get(id).source.getBytes(StandardCharsets.UTF_8));
        };
    }


}
