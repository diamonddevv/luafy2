package dev.diamond.luafy.script.api;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.autodoc.ArgtypeStrings;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.LuaTableBuilder;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.luaj.vm2.LuaValue;

import java.util.concurrent.atomic.AtomicInteger;

public class MinecraftApi extends AbstractScriptApi {
    public MinecraftApi(LuaScript script) {
        super("minecraft", script);
    }

    @Override
    public void addFunctions(FunctionListBuilder builder) {
        builder.add("say", args -> {
            String s = args.arg1().tojstring();

            for (ServerPlayerEntity spe : script.getSource().getServer().getPlayerManager().getPlayerList()) {
                spe.sendMessageToClient(Text.literal(s), false);
            }

            return LuaValue.NIL;
        }, "Prints an unformatted line to the server chat, visible to all players. (similar to /tellraw)", args -> {
            args.add("message", ArgtypeStrings.STRING, "Message to be printed.");
        }, ArgtypeStrings.NIL);

        builder.add("command", args -> {
            String s = args.arg1().tojstring();
            var source = script.getSource().getServer().getCommandSource();
            var cmd = parseCommand(s, source);
            int result = executeCommand(cmd, source);
            return LuaValue.valueOf(result);
        }, "Executes the given command from the server command source. Returns the result of the command.", args -> {
            args.add("command", ArgtypeStrings.STRING, "Command to be executed.");
        }, ArgtypeStrings.INTEGER);

        builder.add("getPlayerFromSelector", args -> {
            String selector = args.arg1().tojstring();
            EntitySelectorReader reader = new EntitySelectorReader(new StringReader(selector), true);
            reader.setIncludesNonPlayers(false);
            EntitySelector s = reader.build();
            try {
                ServerPlayerEntity player = s.getPlayer(script.getSource());
                return LuaTableBuilder.provide(b -> ScriptObjects.PLAYER.toTable(player, b));
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }, "Uses an entity selector to find a player.", args -> {
            args.add("selector", ArgtypeStrings.STRING, "Entity selector");
        }, ScriptObjects.PLAYER.getArgTypeString());
    }

    public static ParseResults<ServerCommandSource> parseCommand(String command, ServerCommandSource source) {
        return source.getDispatcher().parse(command, source);
    }
    public static int executeCommand(ParseResults<ServerCommandSource> command, ServerCommandSource source) {

        try {
            AtomicInteger r = new AtomicInteger();
            source.getDispatcher().setConsumer((context, success, result) -> {
                r.set(result);
            });

            source.getDispatcher().execute(command);

            return r.get();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
