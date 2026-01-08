package dev.diamond.luafy.script.object;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.autodoc.ArglistBuilder;
import dev.diamond.luafy.autodoc.Argtype;
import dev.diamond.luafy.autodoc.SimpleAutodocumentable;
import dev.diamond.luafy.autodoc.FunctionDocInfo;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodNames;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractScriptObject<T> implements SimpleAutodocumentable, Argtype {
    private final String desc;
    private final ScriptObjectDocBuilder docs;

    public AbstractScriptObject(String desc, Consumer<ScriptObjectDocBuilder> docBuilder) {
        this.desc = desc;

        var sodb = new ScriptObjectDocBuilder();
        docBuilder.accept(sodb);
        this.docs = sodb;
    }

    public abstract void toTable(T obj, LuaTableBuilder builder, LuaScript script);
    public abstract T toThing(LuaTable table, ServerCommandSource src, LuaScript script);

    public Optional<AbstractScriptObject<? super T>> getParentType() {
        return Optional.empty();
    }

    public void applyInheritanceToTable(T obj, LuaTableBuilder builder, LuaScript script) {
        Optional<AbstractScriptObject<? super T>> parent = getParentType();
        parent.ifPresent(abstractScriptObject -> abstractScriptObject.toTable(obj, builder, script));
    }

    public static void makeReadonly(LuaTableBuilder builder) {
        builder.addMetamethod(MetamethodNames.NEW_INDEX, args -> {
            Luafy.LOGGER.error("Tried to write to a readonly object!");
            return LuaValue.NIL;
        });
    }

    @Override
    public String generateAutodocString() {
        StringBuilder s = new StringBuilder();

        s.append(getArgtypeString());
        if (getParentType().isPresent()) {
            s.append(" : ").append(getParentType().get().getArgtypeString());
        }
        s.append("\nProperties:\n");
        if (!docs.propertyDocs.isEmpty()) {
            for (var p : docs.propertyDocs) {
                s.append("    - ");
                s.append(p.name);
                s.append(": ");
                s.append(p.type.getArgtypeString());
                s.append(" -> ");
                s.append(p.desc);
                s.append("\n");
            }
        } else {
            s.append("    None\n");
        }

        s.append("\nFunctions:\n");
        if (!docs.functionDocs.isEmpty()) {
            for (var f : docs.functionDocs) {
                s.append(f.generateAutodocString());
                s.append("\n");
            }
        } else {
            s.append("    None\n");
        }

        s.append("\n");

        return s.toString();
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<ScriptObjectDocProperty> getProperties() {
        return docs.propertyDocs;
    }

    public ArrayList<FunctionDocInfo> getFunctions() {
        return docs.functionDocs;
    }

    public static class ScriptObjectDocBuilder {
        private final ArrayList<ScriptObjectDocProperty> propertyDocs;
        private final ArrayList<FunctionDocInfo> functionDocs;

        private ScriptObjectDocBuilder() {
            this.propertyDocs = new ArrayList<>();
            this.functionDocs = new ArrayList<>();
        }

        public void addProperty(String name, Argtype type, String desc) {
            this.propertyDocs.add(new ScriptObjectDocProperty(name, type, desc));
        }

        public void addFunction(String name, String desc, Consumer<ArglistBuilder> arglistBuilder, Argtype returnType) {
            ArglistBuilder args = new ArglistBuilder();
            arglistBuilder.accept(args);
            this.functionDocs.add(new FunctionDocInfo(name, desc, args.args, returnType));
        }
    }

    public record ScriptObjectDocProperty(String name, Argtype type, String desc) {}
}
