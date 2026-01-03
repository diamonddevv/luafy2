package dev.diamond.luafy.script.object;

import dev.diamond.luafy.script.LuaTableBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import org.luaj.vm2.LuaTable;

public class Vec3dScriptObject extends AbstractScriptObject<Vec3d> {

    @Override
    public void toTable(Vec3d obj, LuaTableBuilder builder) {
        builder.add("x", obj.x);
        builder.add("y", obj.y);
        builder.add("z", obj.z);
    }

    @Override
    public Vec3d toThing(LuaTable table, ServerCommandSource src) {
        return new Vec3d(table.get("x").tofloat(), table.get("y").tofloat(), table.get("z").tofloat());
    }

    @Override
    public String getArgTypeString() {
        return "vec3d";
    }
}
