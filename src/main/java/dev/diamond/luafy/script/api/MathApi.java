package dev.diamond.luafy.script.api;

import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.world.phys.Vec3;
import dev.diamond.luafy.lua.LuaTableBuilder;

public class MathApi extends AbstractScriptApi {
    public MathApi(LuaScript script) {
        super("math", script);
    }

    @Override
    public void addFunctions(ScriptApiBuilder apiBuilder) {
        apiBuilder.addGroupless(builder -> {
            builder.add("vec3d", args -> {
                var b = new LuaTableBuilder();
                ScriptObjects.VEC3D.toTable(new Vec3(args.nextFloat(), args.nextFloat(), args.nextFloat()), b, this.script);
                return b.build();
            }, "Creates a 3-component vector object.", b -> {
                b.add("x", Argtypes.NUMBER, "x component");
                b.add("y", Argtypes.NUMBER, "y component");
                b.add("z", Argtypes.NUMBER, "z component");
            }, ScriptObjects.VEC3D);
        });
    }
}
