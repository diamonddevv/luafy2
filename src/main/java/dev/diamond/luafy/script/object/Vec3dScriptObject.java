package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodNames;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;


public class Vec3dScriptObject extends AbstractScriptObject<Vec3d> {


    public Vec3dScriptObject() {
        super("Mathematical 3D Vector", doc -> {
            doc.addProperty("x", Argtypes.NUMBER, "x component");
            doc.addProperty("y", Argtypes.NUMBER, "y component");
            doc.addProperty("z", Argtypes.NUMBER, "z component");
        });
    }

    @Override
    public void toTable(Vec3d obj, LuaTableBuilder builder, LuaScript script) {
        builder.add("x", obj.x);
        builder.add("y", obj.y);
        builder.add("z", obj.z);

        builder.addMetamethod(MetamethodNames.TO_STRING, args -> LuaString.valueOf(Vec3dScriptObject.toString(obj)));
    }

    @Override
    public Vec3d toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return new Vec3d(table.get("x").tofloat(), table.get("y").tofloat(), table.get("z").tofloat());
    }

    @Override
    public String getArgtypeString() {
        return "Vec3d";
    }

    private static String toString(Vec3d obj) {
        return String.format("[%s, %s, %s]", obj.x, obj.y, obj.z);
    }
}
