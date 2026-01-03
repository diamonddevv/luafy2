package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.event.ScriptEvent;
import dev.diamond.luafy.script.ScriptPlugin;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class LuafyRegistries {

    public static RegistryKey<Registry<ScriptPlugin>> SCRIPT_PLUGINS_KEY;
    public static RegistryKey<Registry<ScriptEvent>> SCRIPT_EVENTS_KEY;

    public static Registry<ScriptPlugin> SCRIPT_PLUGINS;
    public static Registry<ScriptEvent> SCRIPT_EVENTS;

    private static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }
    private static <T> RegistryKey<Registry<T>> of(String path) {
        return RegistryKey.ofRegistry(Luafy.id(path));
    }

    public static void register() {
        SCRIPT_PLUGINS_KEY = of("script_plugins");
        SCRIPT_PLUGINS = create(SCRIPT_PLUGINS_KEY);

        SCRIPT_EVENTS_KEY = of("script_events");
        SCRIPT_EVENTS = create(SCRIPT_EVENTS_KEY);
    }
}
