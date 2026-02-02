package dev.diamond.luafy.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class HotCommand {

    public static final String PATH = "luafy_hot_commands";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext buildCtx, Commands.CommandSelection selection) {
        // load files
        // decode with codec
        // register

    }


    public static ArrayList<String> fetchHotCommandSources() {
        Path path = FabricLoaderImpl.INSTANCE.getGameDir().resolve(PATH);
        try {
            Files.walkFileTree(path, new FileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return null;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return null;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return null;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
