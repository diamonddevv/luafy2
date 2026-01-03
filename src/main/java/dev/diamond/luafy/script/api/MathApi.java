package dev.diamond.luafy.script.api;

import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.ArgtypeStrings;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.LuaTableBuilder;
import net.minecraft.util.math.Vec3d;

public class MathApi extends AbstractScriptApi {
    public MathApi(LuaScript script) {
        super("math", script);
    }

    @Override
    public void addFunctions(FunctionListBuilder builder) {
        builder.add("vec3d", args -> {
            var b = new LuaTableBuilder();
            ScriptObjects.VEC3D.toTable(new Vec3d(args.arg(1).tofloat(), args.arg(2).tofloat(), args.arg(3).tofloat()), b);
            return b.build();
        }, "Creates a 3-component vector object.", b -> {
            b.add("x", ArgtypeStrings.NUMBER, "x component");
            b.add("y", ArgtypeStrings.NUMBER, "y component");
            b.add("z", ArgtypeStrings.NUMBER, "z component");
        }, ScriptObjects.VEC3D.getArgTypeString());
    }
}
