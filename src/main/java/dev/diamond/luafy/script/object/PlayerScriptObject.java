package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.ArgtypeStrings;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.lua.LuaTableBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class PlayerScriptObject extends AbstractScriptObject<ServerPlayerEntity> {

    private static final String NAME = "name";
    private static final String UUID = "uuid";
    private static final String GET_POS = "get_pos";
    private static final String TELL = "tell";

    public PlayerScriptObject() {
        super("A player.", doc -> {
            doc.addProperty(NAME, ArgtypeStrings.STRING, "Player's username");
            doc.addProperty(UUID, ArgtypeStrings.STRING, "Player's uuid. Used internally to reference the player back from this LuaValue.");

            doc.addFunction(GET_POS, "Gets the player's current position.", args -> {}, ScriptObjects.VEC3D.getArgTypeString());
            doc.addFunction(TELL, "Prints a line to this player's chat.", args -> {
                args.add("msg", ArgtypeStrings.STRING, "String to display.");
            }, ArgtypeStrings.NIL);
        });
    }

    @Override
    public void toTable(ServerPlayerEntity obj, LuaTableBuilder builder) {
        builder.add(NAME, obj.getStringifiedName());
        builder.add(UUID, obj.getUuidAsString());
        builder.add(GET_POS, args -> LuaTableBuilder.provide(b -> ScriptObjects.VEC3D.toTable(obj.getEntityPos(), b)));
        builder.add(TELL, args -> {
            obj.sendMessageToClient(Text.literal(MetamethodImpl.tostring(args.arg1())), false);
            return LuaValue.NIL;
        });


        makeReadonly(builder);
    }

    @Override
    public ServerPlayerEntity toThing(LuaTable table, ServerCommandSource src) {
        return src.getServer().getPlayerManager().getPlayer(java.util.UUID.fromString(table.get(UUID).tojstring()));
    }

    @Override
    public String getArgTypeString() {
        return "player";
    }
}
