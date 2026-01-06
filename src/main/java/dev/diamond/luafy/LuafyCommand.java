package dev.diamond.luafy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.diamond.luafy.autodoc.Autodocumentable;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.ApiScriptPlugin;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LuafyCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(
          literal("luafy").requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                  .then(
                          literal("eval").then(
                                  argument("src", StringArgumentType.string()).executes(LuafyCommand::eval)
                          )
                  ).then(
                          literal("execute").then(
                                  argument("id", IdentifierArgumentType.identifier()).suggests(new LuafyCommand.ScriptIdsSuggestionProvider()).executes(LuafyCommand::execute)
                          )
                  ).then(
                          literal("autodoc")
                                  .then(
                                          literal("api").then(
                                                  argument("id", IdentifierArgumentType.identifier()).suggests(new ApiPluginIdSuggestionProvider()).executes(LuafyCommand::autodocApi)
                                          )
                                  ).then(
                                          literal("event").then(
                                                  argument("id", IdentifierArgumentType.identifier()).suggests(new EventIdSuggestionProvider()).executes(LuafyCommand::autodocEvent)
                                          )
                                  ).then(
                                          literal("object").then(
                                                  argument("id", IdentifierArgumentType.identifier()).suggests(new ObjectIdSuggestionProvider()).executes(LuafyCommand::autodocObject)
                                          )
                                  )
                  )
        );
    }

    private static int autodocObject(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (LuafyRegistries.SCRIPT_OBJECTS.containsId(id) && LuafyRegistries.SCRIPT_OBJECTS.get(id) instanceof Autodocumentable autodocumentable) {
            ctx.getSource().sendFeedback(() -> Text.literal(autodocumentable.generateAutodoc()), false);
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Object " + id + " does not exist").formatted(Formatting.RED), false);
            return 0;
        }
    }

    private static int autodocEvent(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (LuafyRegistries.SCRIPT_EVENTS.containsId(id) && LuafyRegistries.SCRIPT_EVENTS.get(id) instanceof Autodocumentable autodocumentable) {
            ctx.getSource().sendFeedback(() -> Text.literal(autodocumentable.generateAutodoc()), false);
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Event " + id + " does not exist").formatted(Formatting.RED), false);
            return 0;
        }
    }

    private static int autodocApi(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (LuafyRegistries.SCRIPT_PLUGINS.containsId(id) && LuafyRegistries.SCRIPT_PLUGINS.get(id) instanceof Autodocumentable autodocumentable) {
            ctx.getSource().sendFeedback(() -> Text.literal(autodocumentable.generateAutodoc()), false);
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Api Script Plugin " + id + " does not exist").formatted(Formatting.RED), false);
            return 0;
        }
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

    private static class ApiPluginIdSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.SCRIPT_PLUGINS.getIds()) {
                if (LuafyRegistries.SCRIPT_PLUGINS.get(id) instanceof ApiScriptPlugin<?>) {
                    builder.suggest(id.toString());
                }
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }

    private static class EventIdSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.SCRIPT_EVENTS.getIds()) {
                builder.suggest(id.toString());
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }

    private static class ObjectIdSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.SCRIPT_OBJECTS.getIds()) {
                builder.suggest(id.toString());
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }
}
