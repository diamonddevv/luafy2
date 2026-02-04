package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.autodoc.Argtype;
import dev.diamond.luafy.autodoc.generator.AbstractAutodocGenerator;
import dev.diamond.luafy.script.enumeration.ScriptEnum;
import dev.diamond.luafy.script.event.ScriptEvent;
import dev.diamond.luafy.script.ScriptPlugin;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.luaj.vm2.LuaValue;

public class LuafyRegistries {

    public static ResourceKey<Registry<ScriptPlugin>> SCRIPT_PLUGINS_KEY;
    public static ResourceKey<Registry<Argtype<?, ?>>> SERIALIZABLE_ARGTYPES_KEY;
    public static ResourceKey<Registry<ScriptEvent<?>>> SCRIPT_EVENTS_KEY;
    public static ResourceKey<Registry<AbstractScriptObject<?>>> SCRIPT_OBJECTS_KEY;
    public static ResourceKey<Registry<ScriptEnum<?>>> SCRIPT_ENUMS_KEY;
    public static ResourceKey<Registry<AbstractAutodocGenerator>> AUTODOC_GENERATORS_KEY;

    public static Registry<ScriptPlugin> SCRIPT_PLUGINS;
    public static Registry<Argtype<?, ?>> SERIALIZABLE_ARGTYPES;
    public static Registry<ScriptEvent<?>> SCRIPT_EVENTS;
    public static Registry<AbstractScriptObject<?>> SCRIPT_OBJECTS;
    public static Registry<ScriptEnum<?>> SCRIPT_ENUMS;
    public static Registry<AbstractAutodocGenerator> AUTODOC_GENERATORS;

    private static <T> Registry<T> create(ResourceKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }
    private static <T> ResourceKey<Registry<T>> of(String path) {
        return ResourceKey.createRegistryKey(Luafy.id(path));
    }

    public static void register() {
        SCRIPT_PLUGINS_KEY = of("script_plugins");
        SCRIPT_PLUGINS = create(SCRIPT_PLUGINS_KEY);

        SERIALIZABLE_ARGTYPES_KEY = of("serializable_argtypes");
        SERIALIZABLE_ARGTYPES = create(SERIALIZABLE_ARGTYPES_KEY);

        SCRIPT_EVENTS_KEY = of("script_events");
        SCRIPT_EVENTS = create(SCRIPT_EVENTS_KEY);

        SCRIPT_OBJECTS_KEY = of("script_objects");
        SCRIPT_OBJECTS = create(SCRIPT_OBJECTS_KEY);

        SCRIPT_ENUMS_KEY = of("script_enums");
        SCRIPT_ENUMS = create(SCRIPT_ENUMS_KEY);

        AUTODOC_GENERATORS_KEY = of("autodoc_generators");
        AUTODOC_GENERATORS = create(AUTODOC_GENERATORS_KEY);
    }
}
