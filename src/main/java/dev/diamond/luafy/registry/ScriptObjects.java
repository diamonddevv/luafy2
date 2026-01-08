package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.object.*;
import net.minecraft.registry.Registry;

public class ScriptObjects {

    public static Vec3dScriptObject VEC3D = new Vec3dScriptObject();
    public static EntityScriptObject ENTITY = new EntityScriptObject();
    public static PlayerScriptObject PLAYER = new PlayerScriptObject();
    public static ModScriptObject MOD = new ModScriptObject();
    public static ScriptResultScriptObject SCRIPT_RESULT = new ScriptResultScriptObject();

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("vec3d"), VEC3D);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("entity"), ENTITY);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("player"), PLAYER);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("mod"), MOD);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("script_result"), SCRIPT_RESULT);
    }
}
