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
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.ApiScriptPlugin;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.ScriptExecutionResult;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class LuafyCommand {


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext access, Commands.CommandSelection environment)
    {
        dispatcher.register(
          literal("luafy").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
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
                                          argument("id", IdentifierArgument.id()).suggests(new LuafyCommand.ScriptIdsSuggestionProvider()).executes(LuafyCommand::execute)
                                                  .then(
                                                          argument("ctx", CompoundTagArgument.compoundTag()).executes(ctx -> LuafyCommand.executeWithContext(ctx, false))
                                                                  .then(
                                                                          literal("awaits").executes(ctx -> LuafyCommand.executeWithContext(ctx, true))
                                                                  )
                                                  )
                                  )
                  ).then(
                          literal("autodoc")
                                  .then(
                                          literal("generate").then(
                                                  argument("generator", IdentifierArgument.id()).suggests(new LuafyCommand.AutodocGeneratorIdsSuggestionProvider())
                                                          .then(
                                                                  argument("outputFileName", StringArgumentType.string()).executes(LuafyCommand::generateAutodoc)
                                                          )
                                          )
                                  ).then(
                                          literal("display")
                                                  .then(
                                                          literal("api").then(
                                                                  argument("id", IdentifierArgument.id()).suggests(new ApiPluginIdSuggestionProvider()).executes(LuafyCommand::autodocApi)
                                                          )
                                                  ).then(
                                                          literal("event").then(
                                                                  argument("id", IdentifierArgument.id()).suggests(new EventIdSuggestionProvider()).executes(LuafyCommand::autodocEvent)
                                                          )
                                                  ).then(
                                                          literal("object").then(
                                                                  argument("id", IdentifierArgument.id()).suggests(new ObjectIdSuggestionProvider()).executes(LuafyCommand::autodocObject)
                                                          )
                                                  )
                                  )
                  )
        );
    }

    private static int generateAutodoc(CommandContext<CommandSourceStack> ctx) {
        Identifier id = IdentifierArgument.getId(ctx, "generator");
        String outputFileName = StringArgumentType.getString(ctx, "outputFileName");


        if (LuafyRegistries.AUTODOC_GENERATORS.containsKey(id)) {
            AbstractAutodocGenerator generator = LuafyRegistries.AUTODOC_GENERATORS.getValue(id);
            assert generator != null;
            long startTime = System.currentTimeMillis();

            Path rootpath = FabricLoader.getInstance().getGameDir().resolve("luafy_autodocs");
            String fn = outputFileName + "." + generator.fileExtension;
            String filepath = generator.buildOutput(new File(rootpath.toString(), fn));
            long delta = System.currentTimeMillis() - startTime;
            ctx.getSource().sendSuccess(() -> Component.literal("Autodoc generated at [" + filepath + "] (took " + delta + "ms)"), false);
            return 1;
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Autodoc generator " + id + " does not exist").withStyle(ChatFormatting.RED), false);
            return 0;
        }
    }

    private static int autodocObject(CommandContext<CommandSourceStack> ctx) {
        Identifier id = IdentifierArgument.getId(ctx, "id");
        if (LuafyRegistries.SCRIPT_OBJECTS.containsKey(id) && LuafyRegistries.SCRIPT_OBJECTS.getValue(id) instanceof SimpleAutodocumentable autodocumentable) {
            ctx.getSource().sendSuccess(() -> Component.literal(autodocumentable.generateAutodocString()), false);
            return 1;
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Object " + id + " does not exist").withStyle(ChatFormatting.RED), false);
            return 0;
        }
    }

    private static int autodocEvent(CommandContext<CommandSourceStack> ctx) {
        Identifier id = IdentifierArgument.getId(ctx, "id");
        if (LuafyRegistries.SCRIPT_EVENTS.containsKey(id) && LuafyRegistries.SCRIPT_EVENTS.getValue(id) instanceof SimpleAutodocumentable autodocumentable) {
            ctx.getSource().sendSuccess(() -> Component.literal(autodocumentable.generateAutodocString()), false);
            return 1;
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Event " + id + " does not exist").withStyle(ChatFormatting.RED), false);
            return 0;
        }
    }

    private static int autodocApi(CommandContext<CommandSourceStack> ctx) {
        Identifier id = IdentifierArgument.getId(ctx, "id");
        if (LuafyRegistries.SCRIPT_PLUGINS.containsKey(id) && LuafyRegistries.SCRIPT_PLUGINS.getValue(id) instanceof SimpleAutodocumentable autodocumentable) {
            ctx.getSource().sendSuccess(() -> Component.literal(autodocumentable.generateAutodocString()), false);
            return 1;
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Api Script Plugin " + id + " does not exist").withStyle(ChatFormatting.RED), false);
            return 0;
        }
    }

    private static int executeWithContext(CommandContext<CommandSourceStack> ctx, boolean awaits) {
        Identifier id = IdentifierArgument.getId(ctx, "id");
        CompoundTag context = CompoundTagArgument.getCompoundTag(ctx, "ctx");
        if (Luafy.SCRIPT_MANAGER.has(id)) {

            return execScript(ctx, Luafy.SCRIPT_MANAGER.get(id), "Executed script " + id, LuaTableBuilder.fromNbtCompound(context), awaits);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Script " + id + " does not exist").withStyle(ChatFormatting.RED), true);
            return 0;
        }
    }


    private static int execute(CommandContext<CommandSourceStack> ctx) {
        Identifier id = IdentifierArgument.getId(ctx, "id");
        if (Luafy.SCRIPT_MANAGER.has(id)) {
            return execScript(ctx, Luafy.SCRIPT_MANAGER.get(id), "Executed script " + id, null, false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Script " + id + " does not exist").withStyle(ChatFormatting.RED), true);
            return 0;
        }
    }

    private static int value(CommandContext<CommandSourceStack> ctx) {
        String src = StringArgumentType.getString(ctx, "src");

        try {
            ScriptExecutionResult value = new LuaScript("return " + src).execute(ctx.getSource()).get();
            ctx.getSource().sendSystemMessage(Component.literal(MetamethodImpl.tostring(value.getResult())));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }


    private static int eval(CommandContext<CommandSourceStack> ctx) {
        String src = StringArgumentType.getString(ctx, "src");
        return execScript(ctx, new LuaScript(src), "Successfully executed code; " + src, null, true);
    }


    private static int execScript(CommandContext<CommandSourceStack> ctx, LuaScript script, String message, @Nullable LuaTable contextTable, boolean awaits) {
        Future<ScriptExecutionResult> future = script.execute(ctx.getSource(), contextTable);
        ctx.getSource().sendSuccess(() -> Component.literal(message), true);

        if (awaits) {
            try {
                var result = future.get();
                if (result.success()) {
                    if (result.getResult().isint()) {
                        return result.getResult().toint();
                    }
                    return 1;
                } else {
                    ctx.getSource().sendSuccess(() -> Component.literal("An error occurred executing script; " + result.getError()).withStyle(ChatFormatting.RED), true);
                    return 0;
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return 1;
    }


    private static class ScriptIdsSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (String id : Luafy.SCRIPT_MANAGER.getScriptIdStrings()) {
                if (Luafy.SCRIPT_MANAGER.get(Identifier.parse(id)).onLibPath) continue; // dont suggest libraries
                builder.suggest(id);
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }

    private static class ApiPluginIdSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.SCRIPT_PLUGINS.keySet()) {
                if (LuafyRegistries.SCRIPT_PLUGINS.getValue(id) instanceof ApiScriptPlugin<?>) {
                    builder.suggest(id.toString());
                }
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }

    private static class EventIdSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.SCRIPT_EVENTS.keySet()) {
                builder.suggest(id.toString());
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }

    private static class ObjectIdSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.SCRIPT_OBJECTS.keySet()) {
                builder.suggest(id.toString());
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }

    private static class AutodocGeneratorIdsSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (Identifier id : LuafyRegistries.AUTODOC_GENERATORS.keySet()) {
                builder.suggest(id.toString());
            }

            // Lock the suggestions after we've modified them.
            return builder.buildFuture();
        }
    }
}
