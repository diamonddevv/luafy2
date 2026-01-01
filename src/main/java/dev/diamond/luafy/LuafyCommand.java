package dev.diamond.luafy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LuafyCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(
          literal("luafy").requires(CommandManager.requirePermissionLevel(CommandManager.ADMINS_CHECK))
                  .then(
                          literal("eval").then(
                                  argument("src", StringArgumentType.string()).executes(LuafyCommand::eval)
                          )
                  ).then(
                          literal("execute").then(
                                  argument("id", IdentifierArgumentType.identifier()).suggests(new LuafyCommand.ScriptIdsSuggestionProvider()).executes(LuafyCommand::execute)
                          )
                  )
        );
    }



    private static int execute(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (Luafy.SCRIPT_MANAGER.has(id)) {
            return execScript(ctx, Luafy.SCRIPT_MANAGER.get(id), "Successfully executed script " + id);
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Script " + id + " does not exist").formatted(Formatting.RED), true);
            return 0;
        }
    }


    private static int eval(CommandContext<ServerCommandSource> ctx) {
        String src = StringArgumentType.getString(ctx, "src");
        return execScript(ctx, new LuaScript(src), "Successfully executed code; " + src);
    }


    private static int execScript(CommandContext<ServerCommandSource> ctx, LuaScript script, String successString) {
        LuaScript.Result result = script.execute(ctx.getSource());

        if (result.success()) {
            ctx.getSource().sendFeedback(() -> Text.literal(successString), true);

            if (result.getResult().isint()) {
                return result.getResult().toint();
            }
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("An error occurred executing script; " + result.getError()).formatted(Formatting.RED), true);
            return 0;
        }
    }


    private static class ScriptIdsSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (String id : Luafy.SCRIPT_MANAGER.getScriptIdStrings()) {
                builder.suggest(id);
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }
}
