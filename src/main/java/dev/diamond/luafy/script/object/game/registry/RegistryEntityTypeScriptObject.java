package dev.diamond.luafy.script.object.game.registry;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class RegistryEntityTypeScriptObject extends AbstractScriptObject<EntityType<?>> {

    public static final String SERIAL_PROP_ID = "_id";

    public static final String FUNC_CAN_EXIST_IN_PEACEFUL = "can_exist_in_peaceful";
    public static final String FUNC_FIRE_IMMUNE = "is_fire_immune";
    public static final String FUNC_SPAWN = "spawn";
    public static final String FUNC_GET_ID = "get_id";

    public RegistryEntityTypeScriptObject() {
        super("An entity type.", doc -> {
            doc.addFunction(FUNC_CAN_EXIST_IN_PEACEFUL, "Returns true if this entity can exist in peaceful difficulty.",
                    args -> {}, Argtypes.BOOLEAN);
            doc.addFunction(FUNC_FIRE_IMMUNE, "Returns true if this entity is immune to fire.",
                    args -> {}, Argtypes.BOOLEAN);
            doc.addFunction(FUNC_SPAWN, "Spawns a new instance of this entity.", args -> {
                args.add("pos", ScriptObjects.VEC3D, "Position to spawn at.");
                args.add("dimension", Argtypes.maybe(Argtypes.STRING), "Id of dimension to spawn in.");
            }, ScriptObjects.ENTITY);
            doc.addFunction(FUNC_GET_ID, "Returns the id of this entity type.",
                    args -> {}, Argtypes.STRING);
        });
    }

    @Override
    public void toTable(EntityType<?> obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(SERIAL_PROP_ID, BuiltInRegistries.ENTITY_TYPE.getKey(obj).toString());

        builder.add(FUNC_CAN_EXIST_IN_PEACEFUL, args -> LuaValue.valueOf(obj.isAllowedInPeaceful()));
        builder.add(FUNC_FIRE_IMMUNE, args -> LuaValue.valueOf(obj.fireImmune()));
        builder.add(FUNC_SPAWN, args -> {

            Vec3 pos = args.nextScriptObject(ScriptObjects.VEC3D, script.getSource(), script);
            Level level = args.nextMap(script.getSource().getLevel(), args::getString, s -> {
               Identifier id = Identifier.parse(s);
               return script.lookupRegistry(Registries.DIMENSION, id);
            });


            Entity e = obj.create(level, EntitySpawnReason.COMMAND);
            e.setPos(pos);
            level.addFreshEntity(e);

            return ScriptObjects.ENTITY.provideTable(e, script);
        });

        builder.add(FUNC_GET_ID, args -> LuaValue.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(obj).toString()));
    }

    @Override
    public EntityType<?> toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        Identifier id = Identifier.parse(MetamethodImpl.tostring(table.get(SERIAL_PROP_ID)));
        return BuiltInRegistries.ENTITY_TYPE.getValue(id);
    }

    @Override
    public String getArgtypeString() {
        return "EntityType";
    }
}
