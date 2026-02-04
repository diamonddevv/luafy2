package dev.diamond.luafy.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.autodoc.Argtype;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.LuaScript;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HotCommand {

    public static final String PATH = "luafy_hot_commands";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext buildCtx, Commands.CommandSelection selection) {
        // load files
        List<JsonObject> objs = fetchHotCommandSources();

        Luafy.LOGGER.info("Found {} hot-commands.", objs.size());

        for (JsonObject obj : objs) {
            // decode with codec
            CommandBean bean = CommandBean.CODEC.decode(JsonOps.INSTANCE, obj).resultOrPartial(Luafy.LOGGER::error).orElseThrow().getFirst();

            // build
            ArrayList<RequiredArgumentBuilder<CommandSourceStack, ?>> args = new ArrayList<>();

            for (CommandArgumentBean arg : bean.args) {

                Argtype<?, ?> argtype = LuafyRegistries.SERIALIZABLE_ARGTYPES.getValue(arg.argType);

                if (argtype == null) {
                    throw new RuntimeException("Argtype " + arg.argType + " was not in the registry!");
                }

                RequiredArgumentBuilder<CommandSourceStack, ?> argument = argument(
                        arg.argument,
                        argtype.getCommandArgumentType(buildCtx).orElseThrow(
                                () -> new RuntimeException("Argtype " + arg.argType + " is not deserializable from commands."))
                );

                var suggestor = argtype.suggest();
                if (suggestor.isPresent()) {
                    argument = argument.suggests(suggestor.get());
                }

                args.add(argument);
            }

            // build chain
            var last = args.getLast().executes(ctx -> {

                if (!Luafy.SCRIPT_MANAGER.has(bean.scriptId)) {
                    ctx.getSource().sendSystemMessage(
                            Component.literal("Script with id " + bean.scriptId + " was not found!").withStyle(ChatFormatting.RED)
                    );
                    return 0;
                }
                LuaScript script = Luafy.SCRIPT_MANAGER.get(bean.scriptId);

                // build context table
                var table = new LuaTableBuilder();
                for (CommandArgumentBean arg : bean.args) {

                    Argtype<?, ?> argtype = LuafyRegistries.SERIALIZABLE_ARGTYPES.getValue(arg.argType);

                    if (argtype == null) {
                        ctx.getSource().sendSystemMessage(
                                Component.literal("Argtype " + arg.argType + " was not in the registry!").withStyle(ChatFormatting.RED)
                        );
                        return 0;
                    }

                    Optional<LuaValue> value = (Optional<LuaValue>) argtype.parseCommand(ctx, arg.argument, script);


                    if (value.isPresent()) {
                        table.add(arg.argument, value.get());
                    } else {
                        ctx.getSource().sendSystemMessage(
                                Component.literal("Argument " +
                                        arg.argument +
                                        " of type " +
                                        arg.argType +
                                        " could not be deserialised.").withStyle(ChatFormatting.RED)
                        );
                        return 0;
                    }

                }

                var future = script.execute(
                        ctx.getSource(),
                        table.build()
                );

                if (bean.awaits) {
                    try {
                        var ignored = future.get(); // get to block until done
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }

                return 1;
            });


            for (int i = args.size() - 2; i >= 0; i--) {
                last = args.get(i).then(last);
            }

            var branch = literal(bean.root).then(last);

            // register
            dispatcher.register(branch);
            Luafy.LOGGER.info("Registered hot-command branch '{}'.", bean.root);
        }
    }


    public static List<JsonObject> fetchHotCommandSources() {
        ArrayList<String> paths = new ArrayList<>();
        Path path = FabricLoaderImpl.INSTANCE.getGameDir().resolve(PATH);

        path.toFile().mkdirs();

        try {
            for (Path file : Files.walk(path).toList()) {
                if (Files.isRegularFile(file)) {
                    paths.add(file.toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        return paths.stream().map(p -> {
            try (FileReader reader = new FileReader(p)) {
                return gson.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public static class CommandBean {

        public static final Codec<CommandBean> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("root_command").forGetter(CommandBean::getRootCommand),
                CommandArgumentBean.CODEC.listOf().optionalFieldOf("args", new ArrayList<>()).forGetter(CommandBean::getArgs),
                Identifier.CODEC.fieldOf("script").forGetter(CommandBean::getScriptId),
                Codec.BOOL.optionalFieldOf("awaits", false).forGetter(CommandBean::awaits),
                Codec.BOOL.optionalFieldOf("non_ops_can_run", false).forGetter(CommandBean::nonOpsCanRun)
        ).apply(instance, CommandBean::new));


        public String root;
        public List<CommandArgumentBean> args;
        public Identifier scriptId;
        public boolean awaits;
        public boolean nonOpsCanRun;

        public CommandBean(String root, List<CommandArgumentBean> args, Identifier scriptId, boolean awaits, boolean nonOpsCanRun) {
            this.root = root;
            this.args = args;
            this.scriptId = scriptId;
            this.awaits = awaits;
            this.nonOpsCanRun = nonOpsCanRun;
        }

        public String getRootCommand() {
            return this.root;
        }

        public List<CommandArgumentBean> getArgs() {
            return this.args;
        }

        public Identifier getScriptId() {
            return this.scriptId;
        }

        public boolean awaits() {
            return this.awaits;
        }

        public boolean nonOpsCanRun() {
            return this.nonOpsCanRun;
        }
    }

    public static class CommandArgumentBean {
        public static final Codec<CommandArgumentBean> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("argument").forGetter(CommandArgumentBean::getArgument),
                Identifier.CODEC.fieldOf("arg_type").forGetter(CommandArgumentBean::getArgType)
        ).apply(instance, CommandArgumentBean::new));

        public String argument;
        public Identifier argType;

        public CommandArgumentBean(String argument, Identifier argType) {
            this.argument = argument;
            this.argType = argType;
        }

        public String getArgument() {
            return this.argument;
        }

        public Identifier getArgType() {
            return argType;
        }
    }
}
