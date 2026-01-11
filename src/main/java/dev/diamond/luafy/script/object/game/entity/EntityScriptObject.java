package dev.diamond.luafy.script.object.game.entity;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


public class EntityScriptObject extends AbstractScriptObject<Entity> {

    public static final String PROP_NAME = "name";
    public static final String PROP_UUID = "uuid";
    public static final String FUNC_GET_POS = "get_pos";
    public static final String FUNC_IS_LIVING = "is_living";
    public static final String FUNC_AS_LIVING = "as_living";

    public EntityScriptObject() {
        super("An entity.", doc -> {
            doc.addProperty(PROP_NAME, Argtypes.STRING, "Entity's name.");
            doc.addProperty(PROP_UUID, Argtypes.STRING, "Entity's uuid.");

            doc.addFunction(FUNC_GET_POS, "Gets the entity's current position.", args -> {}, ScriptObjects.VEC3D);
            doc.addFunction(FUNC_IS_LIVING, "Returns true if this entity is a LivingEntity.", args -> {}, Argtypes.BOOLEAN);
            doc.addFunction(FUNC_AS_LIVING, "Return this entity as a LivingEntity.", args -> {}, ScriptObjects.LIVING_ENTITY);
        });
    }

    @Override
    public void toTable(Entity obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_NAME, obj.getStringifiedName());
        builder.add(PROP_UUID, obj.getUuidAsString());
        builder.add(FUNC_GET_POS, args -> LuaTableBuilder.provide(b -> ScriptObjects.VEC3D.toTable(obj.getEntityPos(), b, script)));
        builder.add(FUNC_IS_LIVING, args -> LuaValue.valueOf(obj instanceof LivingEntity));
        builder.add(FUNC_AS_LIVING, args -> LuaTableBuilder.provide(b -> ScriptObjects.LIVING_ENTITY.toTable((LivingEntity) obj, b, script)));
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
