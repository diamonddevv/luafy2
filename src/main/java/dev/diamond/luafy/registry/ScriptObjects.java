package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.object.ModScriptObject;
import dev.diamond.luafy.script.object.PlayerScriptObject;
import dev.diamond.luafy.script.object.Vec3dScriptObject;
import net.minecraft.registry.Registry;

public class ScriptObjects {

    public static Vec3dScriptObject VEC3D = new Vec3dScriptObject();
    public static PlayerScriptObject PLAYER = new PlayerScriptObject();
    public static ModScriptObject MOD = new ModScriptObject();

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("vec3d"), VEC3D);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("player"), PLAYER);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("mod"), MOD);
    }
}
