package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.script.event.ScriptEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.luaj.vm2.LuaValue;


public class ScriptEvents {

    public static ScriptEvent<Object> LOAD = new ScriptEvent<>("Executes after a reload.", b -> {

    }, (b, ctx, script) -> {

    });

    public static ScriptEvent<Object> TICK = new ScriptEvent<>("Executes every server tick.", b -> {

    }, (b, ctx, script) -> {});

    public static ScriptEvent<EntityTakesDamage> ENTITY_TAKES_DAMAGE = new ScriptEvent<>("Executes after an entity takes damage.", b -> {
        b.add("entity", ScriptObjects.LIVING_ENTITY, "Living Entity that took damage.");
        b.add("attacker", Argtypes.maybe(ScriptObjects.ENTITY), "Entity that dealt damage.");
        b.add("damage_taken", Argtypes.NUMBER, "Damage taken.");
        b.add("was_blocked", Argtypes.BOOLEAN, "If true, the damage was blocked.");
    }, (b, ctx, script) -> {
        b.add("entity", ScriptObjects.LIVING_ENTITY.provideTable(ctx.e, script));
        b.add("attacker", ctx.src.getEntity() == null ?
                LuaValue.NIL :
                ScriptObjects.ENTITY.provideTable(ctx.src.getEntity(), script)
        );
        b.add("damage_taken", ctx.damageTaken);

        b.add("was_blocked", ctx.damageTaken);
    });

    public static ScriptEvent<EntityDies> ENTITY_DIES = new ScriptEvent<>("Executes after an entity dies.", b -> {
        b.add("entity", ScriptObjects.LIVING_ENTITY, "Living Entity that died.");
        b.add("attacker", Argtypes.maybe(ScriptObjects.ENTITY), "Entity that killed this one.");
    }, (b, ctx, script) -> {
        b.add("entity", ScriptObjects.LIVING_ENTITY.provideTable(ctx.e, script));
        b.add("attacker", ctx.src.getEntity() == null ?
                LuaValue.NIL :
                ScriptObjects.ENTITY.provideTable(ctx.src.getEntity(), script)
        );

    });



    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("load"), LOAD);
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("tick"), TICK);
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("entity_takes_damage"), ENTITY_TAKES_DAMAGE);
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("entity_dies"), ENTITY_DIES);
    }


    public static void applyEvents() {
        // load
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            LOAD.trigger(server.createCommandSourceStack(), null);
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, throwableIsNull) -> {
            LOAD.trigger(server.createCommandSourceStack(), null);
        });

        // tick
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            TICK.trigger(server.createCommandSourceStack(), null);
        });


        // damage
        ServerLivingEntityEvents.AFTER_DAMAGE.register((e, src, baseDmgTaken, dmgTaken, isBlocked) -> {
            ENTITY_TAKES_DAMAGE.trigger(e.level().getServer().createCommandSourceStack(), new ScriptEvents.EntityTakesDamage(e, src, dmgTaken, isBlocked));
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((e, src) -> {
            ENTITY_DIES.trigger(e.level().getServer().createCommandSourceStack(), new ScriptEvents.EntityDies(e, src));
        });



    }

    public record EntityTakesDamage(LivingEntity e, DamageSource src, float damageTaken, boolean blocked) {}
    public record EntityDies(LivingEntity e, DamageSource src) {}

}
