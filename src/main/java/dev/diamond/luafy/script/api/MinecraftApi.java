package dev.diamond.luafy.script.api;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.server.command.ServerCommandSource;
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
            script.getSource().sendMessage(Text.literal(s));
            return LuaValue.NIL;
        });

        builder.add("command", args -> {
            String s = args.arg1().tojstring();
            var source = script.getSource().getServer().getCommandSource();
            var cmd = parseCommand(s, source);
            int result = executeCommand(cmd, source);
            return LuaValue.valueOf(result);
        });
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
