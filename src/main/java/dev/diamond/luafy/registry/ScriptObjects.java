package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.object.Vec3dScriptObject;
import net.minecraft.registry.Registry;

public class ScriptObjects {

    public static Vec3dScriptObject VEC3D = new Vec3dScriptObject();

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("vec3d"), VEC3D);
    }
}
