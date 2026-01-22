package dev.diamond.luafy.script.object.game.entity;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;


public class LivingEntityScriptObject extends AbstractScriptObject<LivingEntity> {

    public static final String FUNC_GET_HEALTH = "get_health";
    public static final String FUNC_HURT = "hurt";
    public static final String FUNC_KILL = "kill";
    public static final String FUNC_TELEPORT = "teleport";
    public static final String FUNC_GET_ITEMSTACK = "get_stack";

    public LivingEntityScriptObject() {
        super("A living entity.", doc -> {
            doc.addFunction(FUNC_GET_HEALTH, "Returns this entities health.", args -> {}, Argtypes.NUMBER);
            doc.addFunction(FUNC_HURT, "Damages this entity.", args -> {
                args.add("damage_type", Argtypes.STRING, "Identifier of a damage type.");
                args.add("amount", Argtypes.NUMBER, "Amount of damage to deal.");
                args.add("source", Argtypes.maybe(ScriptObjects.ENTITY), "Optional entity that dealt this damage.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_KILL, "Applies infinite damage to this entity with the specified type. If no type is specified, the default damage source is used. Please note that if the entity is invulnerable to the specified damage source, it will not kill them!", args -> {
                args.add("damage_type", Argtypes.maybe(Argtypes.STRING), "Identifier of a damage type.");
                args.add("source", Argtypes.maybe(ScriptObjects.ENTITY), "Optional entity that killed this one.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_TELEPORT, "Teleports this entity to the specified position", args -> {
                args.add("pos", ScriptObjects.VEC3D, "Position to teleport to.");
                args.add("yaw", Argtypes.maybe(Argtypes.NUMBER), "Yaw angle of entity after teleporting. Defaults to current yaw.");
                args.add("pitch", Argtypes.maybe(Argtypes.NUMBER), "Pitch angle of entity after teleporting. Defaults to current pitch.");
                args.add("retain_velocity", Argtypes.maybe(Argtypes.BOOLEAN), "If true, the entity will retain their velocity after teleporting. Defaults to true.");
                args.add("dimension_id", Argtypes.maybe(Argtypes.STRING), "Identifier of dimension to teleport to. Defaults to the entities current dimension.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_GET_ITEMSTACK, "Gets an itemstack from this entities inventory by an inventory slot reference.", args -> {
                args.add("slot_reference", Argtypes.STRING, "Reference to the slot to get the stack from.");
            }, ScriptObjects.ITEM_STACK);
        });
    }

    @Override
    public void toTable(LivingEntity obj, LuaTableBuilder builder, LuaScript script) {
        applyInheritanceToTable(obj, builder, script);

        builder.add(FUNC_GET_HEALTH, args -> obj.getHealth());
        builder.add(FUNC_HURT, args -> {
            Identifier damageTypeId = Identifier.parse(args.nextString());
            float amount = args.nextFloat();
            Optional<Entity> e = Optional.ofNullable(args.nextScriptObject(ScriptObjects.ENTITY, script.getSource(), script, null));

            ServerLevel world = script.getSource().getLevel();
            DamageType type = world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getValue(damageTypeId);
            DamageSource source = new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).wrapAsHolder(type), e.orElse(null));

            obj.hurtServer(world, source, amount);

            return null;
        });
        builder.add(FUNC_KILL, args -> {
            Optional<Identifier> damageTypeId = Optional.ofNullable(args.nextString(null)).map(Identifier::parse);
            Optional<Entity> e = Optional.ofNullable(args.nextScriptObject(ScriptObjects.ENTITY, script.getSource(), script, null));

            ServerLevel world = script.getSource().getLevel();
            DamageType type;

            if (damageTypeId.isPresent()) {
                type = world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getValue(damageTypeId.get());
            } else {
                type = world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getValue(DamageTypes.GENERIC_KILL);
            }
            DamageSource source = new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).wrapAsHolder(type), e.orElse(null));

            obj.hurtServer(world, source, Float.MAX_VALUE);

            return LuaValue.NIL;
        });
        builder.add(FUNC_TELEPORT, args -> {
            Vec3 pos = args.nextScriptObject(ScriptObjects.VEC3D, script.getSource(), script);
            float yaw = args.nextFloat(obj.getYRot());
            float pitch = args.nextFloat(obj.getXRot());
            boolean retainVel = args.nextBoolean(true);

            ResourceKey<Level> registryKey = Optional
                    .ofNullable(args.nextString(null))
                    .map(s -> ResourceKey.create(Registries.DIMENSION, Identifier.parse(s)))
                    .orElse(obj.level().dimension());

            ServerLevel serverWorld = script.getSource().getServer().getLevel(registryKey);


            obj.teleport(new TeleportTransition(
                    serverWorld,
                    pos,
                    retainVel ? obj.getDeltaMovement() : new Vec3(0, 0, 0),
                    yaw,
                    pitch,
                    e -> {}
            ));
            return LuaValue.NIL;
        });

        builder.add(FUNC_GET_ITEMSTACK, args -> {
            String reference = args.nextString();
            SlotAccess access = obj.getSlot(Objects.requireNonNull(SlotRanges.nameToIds(reference)).slots().getFirst());
            ItemStack stack = access != null ? access.get() : ItemStack.EMPTY;
            return LuaTableBuilder.provide(ScriptObjects.ITEM_STACK, stack, script);
        });

        makeReadonly(builder);
    }

    @Override
    public LivingEntity toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return (LivingEntity) ScriptObjects.ENTITY.toThing(table, src, script);
    }

    @Override
    public Class<LivingEntity> getType() {
        return LivingEntity.class;
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
