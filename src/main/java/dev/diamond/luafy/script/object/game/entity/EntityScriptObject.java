package dev.diamond.luafy.script.object.game.entity;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


public class EntityScriptObject extends AbstractScriptObject<Entity> {

    public static final String PROP_UUID = "_uuid";
    public static final String FUNC_GET_POS = "get_pos";
    public static final String FUNC_GET_UUID = "get_uuid";
    public static final String FUNC_GET_NAME = "get_name";
    public static final String FUNC_IS_LIVING = "is_living";
    public static final String FUNC_AS_LIVING = "as_living";

    public EntityScriptObject() {
        super("An entity.", doc -> {
            doc.addFunction(FUNC_GET_POS, "Gets the entity's current position.", args -> {}, ScriptObjects.VEC3D);
            doc.addFunction(FUNC_GET_UUID, "Gets the entity's UUID.", args -> {}, Argtypes.STRING);
            doc.addFunction(FUNC_GET_NAME, "Gets the entity's name.", args -> {}, Argtypes.STRING);
            doc.addFunction(FUNC_IS_LIVING, "Returns true if this entity is a LivingEntity.", args -> {}, Argtypes.BOOLEAN);
            doc.addFunction(FUNC_AS_LIVING, "Return this entity as a LivingEntity.", args -> {}, ScriptObjects.LIVING_ENTITY);
        });
    }


    @Override
    public void toTable(Entity obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_UUID, obj.getStringUUID());

        builder.add(FUNC_GET_POS, args -> LuaTableBuilder.provide(b -> ScriptObjects.VEC3D.toTable(obj.position(), b, script)));
        builder.add(FUNC_GET_UUID, args -> LuaValue.valueOf(obj.getStringUUID()));
        builder.add(FUNC_GET_NAME, args -> LuaValue.valueOf(obj.getPlainTextName()));
        builder.add(FUNC_IS_LIVING, args -> LuaValue.valueOf(obj instanceof LivingEntity));
        builder.add(FUNC_AS_LIVING, args -> LuaTableBuilder.provide(b -> ScriptObjects.LIVING_ENTITY.toTable((LivingEntity) obj, b, script)));
    }

    @Override
    public Entity toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return src.getLevel().getEntity(java.util.UUID.fromString(table.get(PROP_UUID).tojstring()));
    }

    @Override
    public String getArgtypeString() {
        return "Entity";
    }
}
