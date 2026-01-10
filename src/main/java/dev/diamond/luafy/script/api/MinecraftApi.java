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
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
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
                return LuaString.valueOf(SharedConstants.getGameVersion().name());
            }, "Returns the current Minecraft version string.", args -> {}, Argtypes.STRING);

            builder.add("say", args -> {
                String s = MetamethodImpl.tostring(args.arg1());

                for (ServerPlayerEntity spe : script.getSource().getServer().getPlayerManager().getPlayerList()) {
                    spe.sendMessageToClient(Text.literal(s), false);
                }

                script.getGlobals().STDOUT.print(s);
                script.getGlobals().STDOUT.print('\n');

                return LuaValue.NIL;
            }, "Prints an unformatted line to the server chat, visible to all players. (similar to /tellraw). Also prints to the console.", args -> {
                args.add("message", Argtypes.STRING, "Message to be printed.");
            }, Argtypes.NIL);

            builder.add("command", args -> {
                String s = MetamethodImpl.tostring(args.arg1());
                var source = script.getSource().getServer().getCommandSource();
                var cmd = parseCommand(s, source);
                int result = executeCommand(cmd, source);
                return LuaValue.valueOf(result);
            }, "Executes the given command from the server command source. Returns the result of the command.", args -> {
                args.add("command", Argtypes.STRING, "Command to be executed.");
            }, Argtypes.INTEGER);

            builder.add("note", args -> {
                Note note = ScriptEnums.NOTE.fromKey(MetamethodImpl.tostring(args.arg(1)));
                Instrument instrument = ScriptEnums.INSTRUMENT.fromKey(MetamethodImpl.tostring(args.arg(2)));
                Vec3d pos = ScriptObjects.VEC3D.toThing(args.arg(3).checktable(), script.getSource(), this.script);
                boolean particle = args.arg(4).or(LuaBoolean.TRUE).checkboolean();

                ServerWorld world = script.getSource().getWorld();

                int idx = note.getIndex();
                float pitch = note.getPitch();

                world.getServer().execute(() -> { // exec on main thread
                    if (particle)
                        world.spawnParticles(ParticleTypes.NOTE, pos.getX(), pos.getY() + 0.7, pos.getZ(), 1, (double)idx / 24.0, 0.0, 0.0, 0);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), instrument.getInstrument().getSound(), SoundCategory.RECORDS, 3.0F, pitch, world.random.nextLong());
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
                EntitySelectorReader reader = new EntitySelectorReader(new StringReader(selector), true);
                try {
                    EntitySelector s = reader.read();

                    if (s.includesNonPlayers()) {
                        throw EntityArgumentType.PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.create();
                    }

                    if (s.getLimit() > 1) {
                        throw EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION.create();
                    }

                    ServerPlayerEntity player = s.getPlayer(script.getSource());
                    return LuaTableBuilder.provide(b -> ScriptObjects.PLAYER.toTable(player, b, this.script));
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }, "Uses an entity selector to find a player.", args -> {
                args.add("selector", Argtypes.STRING, "Entity selector");
            }, ScriptObjects.PLAYER);

            builder.add("get_entity_from_selector", args -> {
                String selector = MetamethodImpl.tostring(args.arg1());
                EntitySelectorReader reader = new EntitySelectorReader(new StringReader(selector), true);
                try {
                    EntitySelector s = reader.read();

                    if (s.getLimit() > 1) {
                        throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
                    }

                    Entity e = s.getEntity(script.getSource());
                    return LuaTableBuilder.provide(b -> ScriptObjects.ENTITY.toTable(e, b, this.script));
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }, "Uses an entity selector to find an entity.", args -> {
                args.add("selector", Argtypes.STRING, "Entity selector");
            }, ScriptObjects.ENTITY);

            builder.add("get_entities_from_selector", args -> {
                String selector = MetamethodImpl.tostring(args.arg1());
                EntitySelectorReader reader = new EntitySelectorReader(new StringReader(selector), true);
                try {
                    EntitySelector s = reader.read();
                    List<? extends Entity> es = s.getEntities(script.getSource());
                    return LuaTableBuilder.ofArrayTables(
                            es.stream().map(
                                    e -> LuaTableBuilder.provide(
                                            b -> ScriptObjects.ENTITY.toTable(e, b, this.script)
                                    )
                            ).toList()
                    );
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }, "Uses an entity selector to find several entities.", args -> {
                args.add("selector", Argtypes.STRING, "Entity selector");
            }, Argtypes.array(ScriptObjects.ENTITY));
        });
    }

    public static ParseResults<ServerCommandSource> parseCommand(String command, ServerCommandSource source) {
        return source.getDispatcher().parse(command, source);
    }
    public static int executeCommand(ParseResults<ServerCommandSource> command, ServerCommandSource source) {

        try {
            AtomicInteger r = new AtomicInteger();
            source.getDispatcher().setConsumer((context, success, result) -> {
                r.set(result);
            });

            source.getDispatcher().execute(command);

            return r.get();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
