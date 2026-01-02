package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptPlugin;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class LuafyRegistries {

    public static RegistryKey<Registry<ScriptPlugin>> SCRIPT_PLUGINS_KEY;
    public static Registry<ScriptPlugin> SCRIPT_PLUGINS;

    private static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }
    private static <T> RegistryKey<Registry<T>> of(String path) {
        return RegistryKey.ofRegistry(Luafy.id(path));
    }

    public static void register() {
        SCRIPT_PLUGINS_KEY = of("script_plugins");
        SCRIPT_PLUGINS = create(SCRIPT_PLUGINS_KEY);
    }
}
