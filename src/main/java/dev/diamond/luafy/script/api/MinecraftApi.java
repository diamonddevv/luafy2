package dev.diamond.luafy.script.api;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.registry.ScriptEnums;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.script.enumeration.Instrument;
import dev.diamond.luafy.script.enumeration.Note;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MinecraftApi extends AbstractScriptApi {
    public MinecraftApi(LuaScript script) {
        super("minecraft", script);
    }

    @Override
    public void addFunctions(ScriptApiBuilder apiBuilder) {

        apiBuilder.addGroupless(builder -> {

            builder.add("get_version", args -> {
                return LuaString.valueOf(SharedConstants.getCurrentVersion().name());
            }, "Returns the current Minecraft version string.", args -> {}, Argtypes.STRING);

            builder.add("say", args -> {
                String s = MetamethodImpl.tostring(args.arg1());

                for (ServerPlayer spe : script.getSource().getServer().getPlayerList().getPlayers()) {
                    spe.sendSystemMessage(Component.literal(s), false);
                }

                script.getGlobals().STDOUT.print(s);
                script.getGlobals().STDOUT.print('\n');

                return LuaValue.NIL;
            }, "Prints an unformatted line to the server chat, visible to all players. (similar to /tellraw). Also prints to the console.", args -> {
                args.add("message", Argtypes.STRING, "Message to be printed.");
            }, Argtypes.NIL);

            builder.add("command", args -> {
                String s = MetamethodImpl.tostring(args.arg1());
                var source = script.getSource().getServer().createCommandSourceStack();
                var cmd = parseCommand(s, source);
                int result = executeCommand(cmd, source);
                return LuaValue.valueOf(result);
            }, "Executes the given command from the server command source. Returns the result of the command.", args -> {
                args.add("command", Argtypes.STRING, "Command to be executed.");
            }, Argtypes.INTEGER);

            builder.add("note", args -> {
                Note note = ScriptEnums.NOTE.fromKey(MetamethodImpl.tostring(args.arg(1)));
                Instrument instrument = ScriptEnums.INSTRUMENT.fromKey(MetamethodImpl.tostring(args.arg(2)));
                Vec3 pos = ScriptObjects.VEC3D.toThing(args.arg(3).checktable(), script.getSource(), this.script);
                boolean particle = args.arg(4).or(LuaBoolean.TRUE).checkboolean();

                ServerLevel world = script.getSource().getLevel();

                int idx = note.getIndex();
                float pitch = note.getPitch();

                world.getServer().execute(() -> { // exec on main thread
                    if (particle)
                        world.sendParticles(ParticleTypes.NOTE, pos.x(), pos.y() + 0.7, pos.z(), 1, (double)idx / 24.0, 0.0, 0.0, 0);
                    world.playSeededSound(null, pos.x(), pos.y(), pos.z(), instrument.getInstrument().getSoundEvent(), SoundSource.RECORDS, 3.0F, pitch, world.random.nextLong());
                });

                return LuaValue.NIL;
            }, "Plays the specified noteblock note at the given location.", args -> {
                args.add("note", ScriptEnums.NOTE, "Note to play");
                args.add("instrument", ScriptEnums.INSTRUMENT, "Instrument");
                args.add("pos", ScriptObjects.VEC3D, "Location to play sound at");
                args.add("particle", Argtypes.BOOLEAN, "If true, a particle will also render. Defaults to true.");
            }, Argtypes.NIL);

            builder.add("sleep", args -> {
                float seconds = args.arg1().tofloat();
                try {
                    Thread.sleep((long) (seconds * 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return LuaValue.NIL;
            }, "Waits for a given number of seconds before continuing.", args -> {
                args.add("seconds", Argtypes.NUMBER, "Number of seconds to wait.");
            }, Argtypes.NIL);

        });

        apiBuilder.addGroup("entities", builder -> {
            builder.add("get_player_from_selector", args -> {
                String selector = MetamethodImpl.tostring(args.arg1());
                EntitySelectorParser reader = new EntitySelectorParser(new StringReader(selector), true);
                try {
                    EntitySelector s = reader.parse();

                    if (s.getMaxResults() > 1) {
                        throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
                    }

                    ServerPlayer player = s.findSinglePlayer(script.getSource());
                    return LuaTableBuilder.provide(ScriptObjects.PLAYER, player, this.script);
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }, "Uses an entity selector to find a player.", args -> {
                args.add("selector", Argtypes.STRING, "Entity selector");
            }, ScriptObjects.PLAYER);

            builder.add("get_entity_from_selector", args -> {
                String selector = MetamethodImpl.tostring(args.arg1());
                EntitySelectorParser reader = new EntitySelectorParser(new StringReader(selector), true);
                try {
                    EntitySelector s = reader.parse();

                    if (s.getMaxResults() > 1) {
                        throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
                    }

                    Entity e = s.findSingleEntity(script.getSource());
                    return LuaTableBuilder.provide(ScriptObjects.ENTITY, e, this.script);
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }, "Uses an entity selector to find an entity.", args -> {
                args.add("selector", Argtypes.STRING, "Entity selector");
            }, ScriptObjects.ENTITY);

            builder.add("get_entities_from_selector", args -> {
                String selector = MetamethodImpl.tostring(args.arg1());
                EntitySelectorParser reader = new EntitySelectorParser(new StringReader(selector), true);
                try {
                    EntitySelector s = reader.parse();
                    List<? extends Entity> es = s.findEntities(script.getSource());
                    return LuaTableBuilder.ofArrayTables(
                            es.stream().map(
                                    e -> LuaTableBuilder.provide(ScriptObjects.ENTITY, e, this.script)
                            ).toList()
                    );
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }, "Uses an entity selector to find several entities.", args -> {
                args.add("selector", Argtypes.STRING, "Entity selector");
            }, Argtypes.array(ScriptObjects.ENTITY));
        });

        apiBuilder.addGroup("registry", builder -> {

            builder.add("item", args -> {
                Identifier id = Identifier.parse(MetamethodImpl.tostring(args.arg1()));
                Item item = BuiltInRegistries.ITEM.getValue(id);
                return LuaTableBuilder.provide(ScriptObjects.ITEM, item, script);
            }, "Fetches an item type from the registry.", args -> {
                args.add("id", Argtypes.STRING, "Identifier of the item type.");
            }, ScriptObjects.ITEM);

            builder.add("block", args -> {
                Identifier id = Identifier.parse(MetamethodImpl.tostring(args.arg1()));
                Block block = BuiltInRegistries.BLOCK.getValue(id);
                return LuaTableBuilder.provide(ScriptObjects.BLOCK, block, script);
            }, "Fetches an block type from the registry.", args -> {
                args.add("id", Argtypes.STRING, "Identifier of the block type.");
            }, ScriptObjects.BLOCK);

        });

        apiBuilder.addGroup("object", builder -> {

            builder.add("itemstack", args -> {
                Item item = ScriptObjects.ITEM.toThing(args.arg1().checktable(), script.getSource(), script);
                int count = args.arg(2).toint();
                ItemStack stack = new ItemStack(item, count);
                return LuaTableBuilder.provide(ScriptObjects.ITEM_STACK, stack, script);
            }, "Creates an ItemStack from an item and count.", args -> {
                args.add("item", ScriptObjects.ITEM, "Item type.");
                args.add("count", Argtypes.INTEGER, "Count.");
            }, ScriptObjects.ITEM_STACK);


        });
    }

    public static ParseResults<CommandSourceStack> parseCommand(String command, CommandSourceStack source) {
        return source.dispatcher().parse(command, source);
    }
    public static int executeCommand(ParseResults<CommandSourceStack> command, CommandSourceStack source) {

        try {
            AtomicInteger r = new AtomicInteger();
            source.dispatcher().setConsumer((context, success, result) -> {
                r.set(result);
            });

            source.dispatcher().execute(command);

            return r.get();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
