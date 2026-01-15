package dev.diamond.luafy.script.object.game.entity;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerScriptObject extends AbstractScriptObject<ServerPlayer> {

    public static final String FUNC_TELL = "tell";

    public PlayerScriptObject() {
        super("A player.", doc -> {

            doc.addFunction(FUNC_TELL, "Prints a line to this player's chat.", args -> {
                args.add("msg", Argtypes.STRING, "String to display.");
            }, Argtypes.NIL);
        });
    }

    @Override
    public void toTable(ServerPlayer obj, LuaTableBuilder builder, LuaScript script) {
        applyInheritanceToTable(obj, builder, script);

        builder.add(FUNC_TELL, args -> {
            obj.sendSystemMessage(Component.literal(MetamethodImpl.tostring(args.arg1())), false);
            return LuaValue.NIL;
        });

        makeReadonly(builder);
    }

    @Override
    public Optional<AbstractScriptObject<? super ServerPlayer>> getParentType() {
        return Optional.of(ScriptObjects.LIVING_ENTITY);
    }

    @Override
    public ServerPlayer toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return src.getServer().getPlayerList().getPlayer(java.util.UUID.fromString(table.get(EntityScriptObject.PROP_UUID).tojstring()));
    }

    @Override
    public String getArgtypeString() {
        return "Player";
    }
}
