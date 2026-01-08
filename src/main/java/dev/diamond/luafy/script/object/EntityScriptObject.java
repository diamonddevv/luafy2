package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;


public class EntityScriptObject extends AbstractScriptObject<Entity> {

    public static final String PROP_NAME = "name";
    public static final String PROP_UUID = "uuid";
    public static final String FUNC_GET_POS = "get_pos";

    public EntityScriptObject() {
        super("An entity.", doc -> {
            doc.addProperty(PROP_NAME, Argtypes.STRING, "Entity's name.");
            doc.addProperty(PROP_UUID, Argtypes.STRING, "Entity's uuid.");

            doc.addFunction(FUNC_GET_POS, "Gets the entity's current position.", args -> {}, ScriptObjects.VEC3D);
        });
    }

    @Override
    public void toTable(Entity obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_NAME, obj.getStringifiedName());
        builder.add(PROP_UUID, obj.getUuidAsString());
        builder.add(FUNC_GET_POS, args -> LuaTableBuilder.provide(b -> ScriptObjects.VEC3D.toTable(obj.getEntityPos(), b, script)));
    }

    @Override
    public Entity toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return src.getWorld().getEntity(java.util.UUID.fromString(table.get(PROP_UUID).tojstring()));
    }

    @Override
    public String getArgtypeString() {
        return "Entity";
    }
}
