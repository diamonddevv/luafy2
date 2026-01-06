package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.event.ScriptEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;

public class ScriptEvents {

    public static ScriptEvent<Object> TICK = new ScriptEvent<>("Executes every server tick.", b -> {

    }, (src, ctx) -> {

    });

    public static ScriptEvent<LivingEntity> LIVING_ENTITY_DIED = new ScriptEvent<>("Executes after a living entity is killed.", b -> {

    }, (src, ctx) -> {

    });

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("tick"), TICK);
        //Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("living_entity_died"), LIVING_ENTITY_DIED);
    }


    public static void applyEvents() {

        // tick
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            TICK.trigger(server.getCommandSource(), null);
        });

//        // entity
//        ServerLivingEntityEvents.AFTER_DEATH.register((e, src) -> {
//            LIVING_ENTITY_DIED.trigger(e.getEntityWorld().getServer().getCommandSource(), e);
//        });

    }

}
