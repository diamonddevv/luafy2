package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.event.ScriptEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;

public class ScriptEvents {

    public static ScriptEvent<Object> LOAD = new ScriptEvent<>("Executes after a reload.", b -> {

    }, (src, ctx) -> {

    });

    public static ScriptEvent<Object> TICK = new ScriptEvent<>("Executes every server tick.", b -> {

    }, (src, ctx) -> {

    });



    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("load"), LOAD);
        Registry.register(LuafyRegistries.SCRIPT_EVENTS, Luafy.id("tick"), TICK);
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



    }

}
