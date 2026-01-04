package dev.diamond.luafy.script.event;

import dev.diamond.luafy.autodoc.ArgDocInfo;
import dev.diamond.luafy.autodoc.ArglistBuilder;
import dev.diamond.luafy.autodoc.Autodocumentable;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.LuaTableBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScriptEvent<T> implements Autodocumentable {
    private final ArrayList<Identifier> ids;
    private final BiConsumer<T, LuaTableBuilder> ctxBuilder;
    private final String desc;
    private final ArrayList<ArgDocInfo> argList;

    public ScriptEvent(String desc, Consumer<ArglistBuilder> arglistBuilder, BiConsumer<T, LuaTableBuilder> ctxBuilder) {
        this.ids = new ArrayList<>();
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

        for (Identifier id : ids) {
            Luafy.SCRIPT_MANAGER.get(id).execute(src, ctx);
        }
    }

    public void register(Collection<Identifier> ids) {
        this.ids.addAll(ids);
    }

    public void clear() {
        ids.clear();
    }

    @Override
    public String generateAutodoc() {
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
