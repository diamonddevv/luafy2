package dev.diamond.luafy.script.event;

import dev.diamond.luafy.autodoc.ArgDocInfo;
import dev.diamond.luafy.autodoc.ArglistBuilder;
import dev.diamond.luafy.autodoc.SimpleAutodocumentable;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;

public class ScriptEvent<T> implements SimpleAutodocumentable {
    private final ArrayList<ScriptEntry> entries;
    private final ContextBuilder<T> ctxBuilder;
    private final String desc;
    private final ArrayList<ArgDocInfo> argList;
    private boolean isEmpty;

    public ScriptEvent(String desc, Consumer<ArglistBuilder> arglistBuilder, ContextBuilder<T> ctxBuilder) {
        this.entries = new ArrayList<>();
        this.isEmpty = true;
        this.ctxBuilder = ctxBuilder;

        this.desc = desc;

        ArglistBuilder builder = new ArglistBuilder();
        arglistBuilder.accept(builder);
        this.argList = builder.args;

    }

    public void trigger(@NotNull CommandSourceStack src, T context) {
        if (isEmpty) return;

        for (ScriptEntry entry : entries) {
            if (entry.canExecute()) {
                LuaScript script = Luafy.SCRIPT_MANAGER.get(entry.id);
                LuaTableBuilder builder = new LuaTableBuilder();
                script.setSource(src);
                ctxBuilder.build(builder, context, script);
                LuaTable ctx = builder.build();

                entry.setLastResult(script.execute(src, ctx));
            }
        }
    }

    public void register(Collection<ScriptEntry> entries) {
        this.isEmpty = false;
        this.entries.addAll(entries);
    }

    public void clear() {
        this.isEmpty = true;
        this.entries.clear();
    }

    public String getDesc() {
        return this.desc;
    }

    public ArrayList<ArgDocInfo> getArgList() {
        return this.argList;
    }

    @Override
    public String generateAutodocString() {
        StringBuilder s = new StringBuilder();

        Identifier id = LuafyRegistries.SCRIPT_EVENTS.getKey(this);
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
