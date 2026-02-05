package dev.diamond.luafy.script.type.object.game.entity;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.type.object.AbstractScriptObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerScriptObject extends AbstractScriptObject<ServerPlayer> {

    public static final String FUNC_TELL = "tell";
    public static final String FUNC_TELL_COMPONENT = "tell_component";
    public static final String FUNC_GIVE_STACK = "give_stack";

    public PlayerScriptObject() {
        super("A player.", doc -> {

            doc.addFunction(FUNC_TELL, "Prints a line to this player's chat.", args -> {
                args.add("msg", Argtypes.STRING, "String to display.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_TELL_COMPONENT, "Prints a text component to this player's chat.", args -> {
                args.add("msg", ScriptObjects.TEXT_COMPONENT, "Component to display.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_GIVE_STACK, "Gives this player this stack.", args -> {
                args.add("stack", ScriptObjects.ITEM_STACK, "Stack to give.");
            }, Argtypes.NIL);

        });
    }

    @Override
    public void toTable(ServerPlayer obj, LuaTableBuilder builder, LuaScript script) {
        applyInheritanceToTable(obj, builder, script);

        builder.add(FUNC_TELL, args -> {
            obj.sendSystemMessage(Component.literal(args.nextString()), false);
            return LuaValue.NIL;
        });
        builder.add(FUNC_TELL_COMPONENT, args -> {
            obj.sendSystemMessage(args.nextScriptObject(ScriptObjects.TEXT_COMPONENT, script.getSource(), script), false);
            return LuaValue.NIL;
        });
        builder.add(FUNC_GIVE_STACK, args -> {
            ItemStack stack = args.nextScriptObject(ScriptObjects.ITEM_STACK, script.getSource(), script);
            obj.getInventory().add(stack);
           return LuaValue.NIL;
        });



        makeReadonly(builder);
    }

    @Override
    public Optional<AbstractScriptObject<? super ServerPlayer>> getParentType() {
        return Optional.of(ScriptObjects.LIVING_ENTITY);
    }

    @Override
    public ServerPlayer toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return src.getServer().getPlayerList().getPlayer(java.util.UUID.fromString(table.get(EntityScriptObject.PROP_UUID).tojstring()));
    }

    @Override
    public String getArgtypeString() {
        return "Player";
    }

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.of(EntityArgument.player());
    }

    @Override
    public Optional<LuaTable> parseCommandToLua(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        try {
            return Optional.of(
                    provideTable(
                            EntityArgument.getPlayer(cmdCtx, argName),
                            script
                    )
            );
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.empty();
    }
}
