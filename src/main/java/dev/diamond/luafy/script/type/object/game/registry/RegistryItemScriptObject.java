package dev.diamond.luafy.script.type.object.game.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.command.RegistrySuggestionProvider;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodNames;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.type.object.AbstractScriptObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;


public class RegistryItemScriptObject extends AbstractScriptObject<Item> {
    public static final String PROP_ID = "_id";
    public static final String FUNC_CREATE_STACK = "create_stack";

    public RegistryItemScriptObject() {
        super("An item type.", doc -> {

            doc.addFunction(FUNC_CREATE_STACK, "Creates an items stack of this item type.", args -> {
                args.add("count", Argtypes.maybe(Argtypes.INTEGER), "The number of items to create a stack of. Defaults to 1.");
            }, ScriptObjects.ITEM_STACK);

        });
    }

    @Override
    public void toTable(Item obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_ID, BuiltInRegistries.ITEM.getKey(obj).toString());

        builder.add(FUNC_CREATE_STACK, args -> {
            int count = args.nextInt(1);
            ItemStack stack = new ItemStack(obj, count);
            return LuaTableBuilder.provide(ScriptObjects.ITEM_STACK, stack, script);
        });

        builder.addMetamethod(MetamethodNames.TO_STRING, args -> LuaValue.valueOf(BuiltInRegistries.ITEM.getKey(obj).toString()));
    }

    @Override
    public Item toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(table.get(PROP_ID).tojstring()));
    }

    @Override
    public String getArgtypeString() {
        return "Item";
    }

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.of(IdentifierArgument.id());
    }

    @Override
    public Optional<LuaTable> parseCommand(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        return Optional.of(
                provideTable(
                        BuiltInRegistries.ITEM.getValue(
                                IdentifierArgument.getId(cmdCtx, argName)
                        ),
                        script
                )
        );
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.of(new RegistrySuggestionProvider<>(BuiltInRegistries.ITEM));
    }
}
