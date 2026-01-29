package dev.diamond.luafy.script.object.game.entity;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.api.MinecraftApi;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


public class EntityScriptObject extends AbstractScriptObject<Entity> {

    public static final String PROP_UUID = "_uuid";
    public static final String FUNC_GET_POS = "get_pos";
    public static final String FUNC_GET_UUID = "get_uuid";
    public static final String FUNC_GET_NAME = "get_name";
    public static final String FUNC_GET_TYPE_ID = "get_type_id";
    public static final String FUNC_GET_TYPE = "get_type";
    public static final String FUNC_IS_LIVING = "is_living";
    public static final String FUNC_AS_LIVING = "as_living";
    public static final String FUNC_IS_PLAYER = "is_player";
    public static final String FUNC_AS_PLAYER = "as_player";
    public static final String FUNC_EXECUTE_AS = "execute_as";

    public EntityScriptObject() {
        super("An entity.", doc -> {
            doc.addFunction(FUNC_GET_POS, "Gets the entity's current position.", args -> {}, ScriptObjects.VEC3D);
            doc.addFunction(FUNC_GET_UUID, "Gets the entity's UUID.", args -> {}, Argtypes.STRING);
            doc.addFunction(FUNC_GET_NAME, "Gets the entity's name.", args -> {}, Argtypes.STRING);
            doc.addFunction(FUNC_GET_TYPE_ID, "Gets the id of the entity type that this entity is.", args -> {}, Argtypes.STRING);
            doc.addFunction(FUNC_GET_TYPE, "Gets the the entity type that this entity is.", args -> {}, ScriptObjects.REGISTRY_ENTITY_TYPE);
            doc.addFunction(FUNC_IS_LIVING, "Returns true if this entity is a LivingEntity.", args -> {}, Argtypes.BOOLEAN);
            doc.addFunction(FUNC_AS_LIVING, "Return this entity as a LivingEntity.", args -> {}, ScriptObjects.LIVING_ENTITY);
            doc.addFunction(FUNC_IS_PLAYER, "Returns true if this entity is a PlayerEntity.", args -> {}, Argtypes.BOOLEAN);
            doc.addFunction(FUNC_AS_PLAYER, "Return this entity as a PlayerEntity.", args -> {}, ScriptObjects.PLAYER);
            doc.addFunction(FUNC_EXECUTE_AS, "Execute a commmand as this entity.", args -> {
                args.add("command", Argtypes.STRING, "The command to execute.");
            }, Argtypes.INTEGER);
        });
    }


    @Override
    public void toTable(Entity obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_UUID, obj.getStringUUID());

        builder.add(FUNC_GET_POS, args -> LuaTableBuilder.provide(b -> ScriptObjects.VEC3D.toTable(obj.position(), b, script)));
        builder.add(FUNC_GET_UUID, args -> LuaValue.valueOf(obj.getStringUUID()));
        builder.add(FUNC_GET_NAME, args -> LuaValue.valueOf(obj.getPlainTextName()));
        builder.add(FUNC_GET_TYPE_ID, args -> LuaValue.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(obj.getType()).toString()));
        builder.add(FUNC_GET_TYPE, args -> ScriptObjects.REGISTRY_ENTITY_TYPE.provideTable(obj.getType(), script));
        builder.add(FUNC_IS_LIVING, args -> LuaValue.valueOf(obj instanceof LivingEntity));
        builder.add(FUNC_AS_LIVING, args -> LuaTableBuilder.provide(b -> ScriptObjects.LIVING_ENTITY.toTable((LivingEntity) obj, b, script)));
        builder.add(FUNC_IS_PLAYER, args -> LuaValue.valueOf(obj instanceof ServerPlayer));
        builder.add(FUNC_AS_PLAYER, args -> LuaTableBuilder.provide(b -> ScriptObjects.PLAYER.toTable((ServerPlayer) obj, b, script)));
        builder.add(FUNC_EXECUTE_AS, args -> {
            String s = args.nextString();
            var source = script.getSource().withEntity(obj);

            var cmd = MinecraftApi.parseCommand(s, source);
            int result = MinecraftApi.executeCommand(cmd, source);
            return LuaValue.valueOf(result);
        });
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
