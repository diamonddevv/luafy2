package dev.diamond.luafy.script.object;

import dev.diamond.luafy.Autodocumentable;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.LuaTableBuilder;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;

public abstract class AbstractScriptObject<T> implements Autodocumentable {
    public AbstractScriptObject() {
    }

    public abstract void toTable(T obj, LuaTableBuilder builder);
    public abstract T toThing(LuaTable table, ServerCommandSource src);
    public abstract String getArgTypeString();

    @Override
    public String generateAutodoc() {
        StringBuilder s = new StringBuilder();

        var id = LuafyRegistries.SCRIPT_OBJECTS.getId(this);
        s.append(getArgTypeString());
        s.append(" - ");
        s.append(id);

        s.append("\n");

        return s.toString();
    }
}
