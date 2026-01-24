package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodNames;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;


public class Vec3dScriptObject extends AbstractScriptObject<Vec3> {

    public static final String PROP_X = "x";
    public static final String PROP_Y = "y";
    public static final String PROP_Z = "z";

    public Vec3dScriptObject() {
        super("Mathematical 3D Vector", doc -> {
            doc.addProperty(PROP_X, Argtypes.NUMBER, "x component");
            doc.addProperty(PROP_Y, Argtypes.NUMBER, "y component");
            doc.addProperty(PROP_Z, Argtypes.NUMBER, "z component");
        });
    }

    @Override
    public void toTable(Vec3 obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_X, obj.x);
        builder.add(PROP_Y, obj.y);
        builder.add(PROP_Z, obj.z);

        builder.addMetamethod(MetamethodNames.TO_STRING, args -> LuaString.valueOf(Vec3dScriptObject.toString(obj)));
    }

    @Override
    public Vec3 toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return new Vec3(table.get(PROP_X).tofloat(), table.get(PROP_Y).tofloat(), table.get(PROP_Z).tofloat());
    }

    @Override
    public String getArgtypeString() {
        return "Vec3d";
    }

    private static String toString(Vec3 obj) {
        return String.format("[%s, %s, %s]", obj.x, obj.y, obj.z);
    }
}
