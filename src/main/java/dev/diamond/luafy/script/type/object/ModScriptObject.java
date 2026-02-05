package dev.diamond.luafy.script.type.object;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.command.StringListSuggestionProvider;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;

public class ModScriptObject extends AbstractScriptObject<ModContainer> {

    public static final String FUNC_MODID = "get_mod_id";
    public static final String FUNC_VERSION = "get_version";

    public ModScriptObject() {
        super("An object representing a mod installed on the server.", doc -> {
            doc.addFunction(FUNC_MODID, "Gets the id of this mod.", args -> {},  Argtypes.STRING);
            doc.addFunction(FUNC_VERSION, "The version of the mod currently installed.", args -> {},  Argtypes.STRING);
        });
    }

    @Override
    public void toTable(ModContainer obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(FUNC_MODID, args -> LuaValue.valueOf(obj.getMetadata().getId()));
        builder.add(FUNC_VERSION, args -> LuaValue.valueOf(obj.getMetadata().getVersion().getFriendlyString()));

        makeReadonly(builder);
    }

    @Override
    public ModContainer toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return FabricLoader.getInstance().getModContainer(table.get(FUNC_MODID).tojstring()).orElseThrow();
    }

    @Override
    public String getArgtypeString() {
        return "Mod";
    }

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.of(StringArgumentType.word());
    }

    @Override
    public Optional<LuaTable> parseCommand(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        return Optional.of(
                provideTable(FabricLoader.getInstance().getModContainer(
                        StringArgumentType.getString(cmdCtx, argName)
                ).orElseThrow(), script)
        );
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.of(new StringListSuggestionProvider(FabricLoader
                .getInstance()
                .getAllMods()
                .stream()
                .map(mod -> mod.getMetadata().getId())
                .toList()
        ));
    }
}

