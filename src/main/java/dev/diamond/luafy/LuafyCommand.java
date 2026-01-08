package dev.diamond.luafy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.diamond.luafy.autodoc.SimpleAutodocumentable;
import dev.diamond.luafy.autodoc.generator.AbstractAutodocGenerator;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.ApiScriptPlugin;
import dev.diamond.luafy.script.LuaScript;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
                          literal("value").then(
                                  argument("src", StringArgumentType.string()).executes(LuafyCommand::value)
                          )
                  ).then(
                          literal("execute")
                                  .then(
                                          argument("id", IdentifierArgumentType.identifier()).suggests(new LuafyCommand.ScriptIdsSuggestionProvider()).executes(LuafyCommand::execute)
                                                  .then(
                                                          argument("ctx", NbtCompoundArgumentType.nbtCompound()).executes(ctx -> LuafyCommand.executeWithContext(ctx, false))
                                                                  .then(
                                                                          literal("awaits").executes(ctx -> LuafyCommand.executeWithContext(ctx, true))
                                                                  )
                                                  )
                                  )
                  ).then(
                          literal("autodoc")
                                  .then(
                                          literal("generate").then(
                                                  argument("generator", IdentifierArgumentType.identifier()).suggests(new LuafyCommand.AutodocGeneratorIdsSuggestionProvider())
                                                          .then(
                                                                  argument("outputFileName", StringArgumentType.string()).executes(LuafyCommand::generateAutodoc)
                                                          )
                                          )
                                  ).then(
                                          literal("display")
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
                  )
        );
    }

    private static int generateAutodoc(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "generator");
        String outputFileName = StringArgumentType.getString(ctx, "outputFileName");


        if (LuafyRegistries.AUTODOC_GENERATORS.containsId(id)) {
            AbstractAutodocGenerator generator = LuafyRegistries.AUTODOC_GENERATORS.get(id);
            assert generator != null;
            long startTime = System.currentTimeMillis();

            Path rootpath = FabricLoader.getInstance().getGameDir().resolve("luafy_autodocs");
            String fn = outputFileName + "." + generator.fileExtension;
            String filepath = generator.buildOutput(new File(rootpath.toString(), fn));
            long delta = System.currentTimeMillis() - startTime;
            ctx.getSource().sendFeedback(() -> Text.literal("Autodoc generated at [" + filepath + "] (took " + delta + "ms)"), false);
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Autodoc generator " + id + " does not exist").formatted(Formatting.RED), false);
            return 0;
        }
    }

    private static int autodocObject(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (LuafyRegistries.SCRIPT_OBJECTS.containsId(id) && LuafyRegistries.SCRIPT_OBJECTS.get(id) instanceof SimpleAutodocumentable autodocumentable) {
            ctx.getSource().sendFeedback(() -> Text.literal(autodocumentable.generateAutodocString()), false);
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Object " + id + " does not exist").formatted(Formatting.RED), false);
            return 0;
        }
    }

    private static int autodocEvent(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (LuafyRegistries.SCRIPT_EVENTS.containsId(id) && LuafyRegistries.SCRIPT_EVENTS.get(id) instanceof SimpleAutodocumentable autodocumentable) {
            ctx.getSource().sendFeedback(() -> Text.literal(autodocumentable.generateAutodocString()), false);
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Event " + id + " does not exist").formatted(Formatting.RED), false);
            return 0;
        }
    }

    private static int autodocApi(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (LuafyRegistries.SCRIPT_PLUGINS.containsId(id) && LuafyRegistries.SCRIPT_PLUGINS.get(id) instanceof SimpleAutodocumentable autodocumentable) {
            ctx.getSource().sendFeedback(() -> Text.literal(autodocumentable.generateAutodocString()), false);
            return 1;
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Api Script Plugin " + id + " does not exist").formatted(Formatting.RED), false);
            return 0;
        }
    }

    private static int executeWithContext(CommandContext<ServerCommandSource> ctx, boolean awaits) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        NbtCompound context = NbtCompoundArgumentType.getNbtCompound(ctx, "ctx");
        if (Luafy.SCRIPT_MANAGER.has(id)) {

            return execScript(ctx, Luafy.SCRIPT_MANAGER.get(id), "Executed script " + id, LuaTableBuilder.fromNbtCompound(context), awaits);
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Script " + id + " does not exist").formatted(Formatting.RED), true);
            return 0;
        }
    }


    private static int execute(CommandContext<ServerCommandSource> ctx) {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        if (Luafy.SCRIPT_MANAGER.has(id)) {
            return execScript(ctx, Luafy.SCRIPT_MANAGER.get(id), "Executed script " + id, null, false);
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Script " + id + " does not exist").formatted(Formatting.RED), true);
            return 0;
        }
    }

    private static int value(CommandContext<ServerCommandSource> ctx) {
        String src = StringArgumentType.getString(ctx, "src");
        return execScript(ctx, new LuaScript("minecraft.get_player_from_selector(\"@s\").tell(" + src + ")"), "", null, false);
    }


    private static int eval(CommandContext<ServerCommandSource> ctx) {
        String src = StringArgumentType.getString(ctx, "src");
        return execScript(ctx, new LuaScript(src), "Successfully executed code; " + src, null, true);
    }


    private static int execScript(CommandContext<ServerCommandSource> ctx, LuaScript script, String message, @Nullable LuaTable contextTable, boolean awaits) {
        Future<LuaScript.Result> future = script.execute(ctx.getSource(), contextTable);
        ctx.getSource().sendFeedback(() -> Text.literal(message), true);

        if (awaits) {
            try {
                var result = future.get();
                if (result.success()) {
                    if (result.getResult().isint()) {
                        return result.getResult().toint();
                    }
                    return 1;
                } else {
                    ctx.getSource().sendFeedback(() -> Text.literal("An error occurred executing script; " + result.getError()).formatted(Formatting.RED), true);
                    return 0;
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return 1;
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

    private static class AutodocGeneratorIdsSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.AUTODOC_GENERATORS.getIds()) {
                builder.suggest(id.toString());
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }
}
