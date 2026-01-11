package dev.diamond.luafy.script.object.game.entity;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;


public class LivingEntityScriptObject extends AbstractScriptObject<LivingEntity> {

    public static final String FUNC_GET_HEALTH = "get_health";
    public static final String FUNC_HURT = "hurt";
    public static final String FUNC_KILL = "kill";
    public static final String FUNC_TELEPORT = "teleport";

    public LivingEntityScriptObject() {
        super("A living entity.", doc -> {
            doc.addFunction(FUNC_GET_HEALTH, "Returns this entities health.", args -> {}, Argtypes.NUMBER);
            doc.addFunction(FUNC_HURT, "Damages this entity.", args -> {
                args.add("damage_type", Argtypes.STRING, "Identifier of a damage type.");
                args.add("amount", Argtypes.NUMBER, "Amount of damage to deal.");
                args.add("source", Argtypes.maybe(ScriptObjects.ENTITY), "Optional entity that dealt this damage.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_KILL, "Kills this entity.", args -> {
                args.add("damage_type", Argtypes.STRING, "Identifier of a damage type.");
                args.add("source", Argtypes.maybe(ScriptObjects.ENTITY), "Optional entity that killed this one.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_TELEPORT, "Teleports this entity to the specified position", args -> {
                args.add("pos", ScriptObjects.VEC3D, "Position to teleport to.");
                args.add("yaw", Argtypes.maybe(Argtypes.NUMBER), "Yaw angle of entity after teleporting. Defaults to current yaw.");
                args.add("pitch", Argtypes.maybe(Argtypes.NUMBER), "Pitch angle of entity after teleporting. Defaults to current pitch.");
                args.add("retain_velocity", Argtypes.maybe(Argtypes.BOOLEAN), "If true, the entity will retain their velocity after teleporting. Defaults to true.");
                args.add("dimension_id", Argtypes.maybe(Argtypes.STRING), "Identifier of dimension to teleport to. Defaults to the entities current dimension.");
            }, Argtypes.NIL);
        });
    }

    @Override
    public void toTable(LivingEntity obj, LuaTableBuilder builder, LuaScript script) {
        applyInheritanceToTable(obj, builder, script);

        builder.add(FUNC_GET_HEALTH, args -> LuaValue.valueOf(obj.getHealth()));
        builder.add(FUNC_HURT, args -> {
            Identifier damageTypeId = Identifier.of(MetamethodImpl.tostring(args.arg(1)));
            float amount = args.arg(2).tofloat();
            Optional<Entity> e = args.arg(3).isnil() ?
                    Optional.empty() : Optional.of(ScriptObjects.ENTITY.toThing(args.arg(3).checktable(), script.getSource(), script));

            ServerWorld world = script.getSource().getWorld();
            DamageType type = world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).get(damageTypeId);
            DamageSource source = new DamageSource(world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(type), e.orElse(null));

            obj.damage(world, source, amount);

            return LuaValue.NIL;
        });
        builder.add(FUNC_KILL, args -> {
            Identifier damageTypeId = Identifier.of(MetamethodImpl.tostring(args.arg(1)));
            Optional<Entity> e = args.arg(2).isnil() ?
                    Optional.empty() : Optional.of(ScriptObjects.ENTITY.toThing(args.arg(2).checktable(), script.getSource(), script));

            ServerWorld world = script.getSource().getWorld();
            DamageType type = world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).get(damageTypeId);
            DamageSource source = new DamageSource(world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(type), e.orElse(null));

            obj.damage(world, source, Float.MAX_VALUE);

            return LuaValue.NIL;
        });
        builder.add(FUNC_TELEPORT, args -> {
            Vec3d pos = ScriptObjects.VEC3D.toThing(args.arg(1).checktable(), script.getSource(), script);
            float yaw = args.arg(2).or(LuaValue.valueOf(obj.getYaw())).tofloat();
            float pitch = args.arg(3).or(LuaValue.valueOf(obj.getPitch())).tofloat();
            boolean retainVel = args.arg(4).or(LuaValue.valueOf(true)).toboolean();

            RegistryKey<World> registryKey = args.arg(5).isnil() ?
                    obj.getEntityWorld().getRegistryKey() : RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MetamethodImpl.tostring(args.arg(5))));

            ServerWorld serverWorld = script.getSource().getServer().getWorld(registryKey);


            obj.teleportTo(new TeleportTarget(
                    serverWorld,
                    pos,
                    retainVel ? obj.getVelocity() : new Vec3d(0, 0, 0),
                    yaw,
                    pitch,
                    e -> {}
            ));
            return LuaValue.NIL;
        });

        makeReadonly(builder);
    }

    @Override
    public LivingEntity toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return (LivingEntity) ScriptObjects.ENTITY.toThing(table, src, script);
    }

    @Override
    public Optional<AbstractScriptObject<? super LivingEntity>> getParentType() {
        return Optional.of(ScriptObjects.ENTITY);
    }

    @Override
    public String getArgtypeString() {
        return "LivingEntity";
    }
}
