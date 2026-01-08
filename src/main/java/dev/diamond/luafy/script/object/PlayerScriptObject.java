package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;

public class PlayerScriptObject extends AbstractScriptObject<ServerPlayerEntity> {

    public static final String FUNC_TELL = "tell";

    public PlayerScriptObject() {
        super("A player.", doc -> {

            doc.addFunction(FUNC_TELL, "Prints a line to this player's chat.", args -> {
                args.add("msg", Argtypes.STRING, "String to display.");
            }, Argtypes.NIL);
        });
    }

    @Override
    public void toTable(ServerPlayerEntity obj, LuaTableBuilder builder, LuaScript script) {
        applyInheritanceToTable(obj, builder, script);

        builder.add(FUNC_TELL, args -> {
            obj.sendMessageToClient(Text.literal(MetamethodImpl.tostring(args.arg1())), false);
            return LuaValue.NIL;
        });

        makeReadonly(builder);
    }

    @Override
    public Optional<AbstractScriptObject<? super ServerPlayerEntity>> getParentType() {
        return Optional.of(ScriptObjects.ENTITY);
    }

    @Override
    public ServerPlayerEntity toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return src.getServer().getPlayerManager().getPlayer(java.util.UUID.fromString(table.get(EntityScriptObject.PROP_UUID).tojstring()));
    }

    @Override
    public String getArgtypeString() {
        return "Player";
    }
}
