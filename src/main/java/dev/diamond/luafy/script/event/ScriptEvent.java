package dev.diamond.luafy.script.event;

import dev.diamond.luafy.autodoc.ArgDocInfo;
import dev.diamond.luafy.autodoc.ArglistBuilder;
import dev.diamond.luafy.autodoc.SimpleAutodocumentable;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.ScriptExecutionResult;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScriptEvent<T> implements SimpleAutodocumentable {
    private final ArrayList<ScriptEntry> entries;
    private final BiConsumer<T, LuaTableBuilder> ctxBuilder;
    private final String desc;
    private final ArrayList<ArgDocInfo> argList;

    public ScriptEvent(String desc, Consumer<ArglistBuilder> arglistBuilder, BiConsumer<T, LuaTableBuilder> ctxBuilder) {
        this.entries = new ArrayList<>();
        this.ctxBuilder = ctxBuilder;

        this.desc = desc;

        ArglistBuilder builder = new ArglistBuilder();
        arglistBuilder.accept(builder);
        this.argList = builder.args;

    }

    public void trigger(@NotNull ServerCommandSource src, T context) {
        LuaTableBuilder builder = new LuaTableBuilder();
        ctxBuilder.accept(context, builder);
        LuaTable ctx = builder.build();

        for (ScriptEntry entry : entries) {
            if (entry.canExecute()) {
                entry.setLastResult(Luafy.SCRIPT_MANAGER.get(entry.id).execute(src, ctx));
            }
        }
    }

    public void register(Collection<ScriptEntry> entries) {
        this.entries.addAll(entries);
    }

    public void clear() {
        entries.clear();
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public String generateAutodocString() {
        StringBuilder s = new StringBuilder();

        Identifier id = LuafyRegistries.SCRIPT_EVENTS.getId(this);
        s.append(id);
        s.append("\n");
        s.append(this.desc);
        s.append("\nContext Arguments:\n");
        if (!this.argList.isEmpty()) {
            for (var arg : this.argList) {
                s.append("    - ");
                s.append(arg.argName());
                s.append(": ");
                s.append(arg.argType());
                s.append(" -> ");
                s.append(arg.argDesc());
                s.append("\n");
            }
        } else {
            s.append("    None\n");
        }
        s.append("\n");

        return s.toString();
    }
}
