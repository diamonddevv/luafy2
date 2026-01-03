package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.event.ScriptEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registry;

public class ScriptEvents {

    public static ScriptEvent<Object> TICK = new ScriptEvent<>("Executes every server tick.", b -> {

    }, (src, ctx) -> {

    });

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("tick"), TICK);
    }


    public static void applyEvents() {

        // tick
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            TICK.trigger(server.getCommandSource(), null);
        });

    }

}
