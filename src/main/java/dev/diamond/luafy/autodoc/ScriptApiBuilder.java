package dev.diamond.luafy.autodoc;

import org.luaj.vm2.LuaTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

public class ScriptApiBuilder {

    public static final String GROUPLESS_GROUP = "";

    private final HashMap<String, FunctionListBuilder> groups;

    public ScriptApiBuilder() {
        this.groups = new HashMap<>();
    }



    public void addGroup(String group, Consumer<FunctionListBuilder> builder) {
        if (group.equals(GROUPLESS_GROUP)) {
            throw new IllegalStateException("Can't have group with empty name!");
        }

        var b = new FunctionListBuilder();
        builder.accept(b);

        groups.put(group, b);
    }

    public void addGroupless(Consumer<FunctionListBuilder> builder) {
        var b = new FunctionListBuilder();
        builder.accept(b);
        groups.put(GROUPLESS_GROUP, b);
    }

    public void build(LuaTable table) {
        for (String group : groups.keySet()) {
            if (group.equals(GROUPLESS_GROUP)) {
                groups.get(group).build(table);
            } else {
                LuaTable groupTable = LuaTable.tableOf();
                groups.get(group).build(groupTable);
                table.set(group, groupTable);
            }
        }
    }


    public HashMap<String, Collection<FunctionDocInfo>> getDocumentation() {
        HashMap<String, Collection<FunctionDocInfo>> docs = new HashMap<>();
        for (String group : groups.keySet()) {
            docs.put(group, groups.get(group).getDocumentation());
        }
        return docs;
    }
}
