package dev.diamond.luafy.script.object.game;

import com.mojang.serialization.JsonOps;
import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class TextComponentScriptObject extends AbstractScriptObject<MutableComponent> {

    public static final String PROP_POINTER = "_ptr";

    public static final String FUNC_SERIALISE = "serialise";

    public static final String FUNC_APPEND_LITERAL = "append_literal";
    public static final String FUNC_APPEND_TRANSLATABLE = "append_translatable";
    public static final String FUNC_APPEND_SPRITE = "append_sprite";
    public static final String FUNC_APPEND_PLAYER_SPRITE = "append_sprite_player";

    public TextComponentScriptObject() {
        super("Text component.", doc -> {
            doc.addFunction(FUNC_SERIALISE, "Serialises this text component to a JSON string.", args -> {}, Argtypes.STRING);

            doc.addFunction(FUNC_APPEND_LITERAL, "Appends as literal text.", args -> {
                args.add("string", Argtypes.STRING, "Literal text.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_APPEND_TRANSLATABLE, "Appends as translatable text.", args -> {
                args.add("string", Argtypes.STRING, "Translatable text.");
                args.add("args", Argtypes.maybe(Argtypes.array(ScriptObjects.TEXT_COMPONENT)), "Components to pass as objects.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_APPEND_SPRITE, "Appends a sprite.", args -> {
                args.add("atlas", Argtypes.STRING, "Sprite atlas.");
                args.add("id", Argtypes.STRING, "Sprite identifer.");
            }, Argtypes.NIL);
            doc.addFunction(FUNC_APPEND_PLAYER_SPRITE, "Appends as a playerhead sprite.", args -> {
                args.add("username", Argtypes.STRING, "Player username.");
            }, Argtypes.NIL);

        });
    }

    @Override
    public void toTable(MutableComponent obj, LuaTableBuilder builder, LuaScript script) {

        // i probably could serialise this, but i think this would be faster
        builder.add(PROP_POINTER, script.addUnserializableData(obj));


        builder.add(FUNC_SERIALISE, args -> {
            var json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, obj);
            return LuaValue.valueOf(json.getOrThrow().toString());
        });


        // append functions
        builder.add(FUNC_APPEND_LITERAL, args -> {
            obj.append(Component.literal(args.nextString()));
            return LuaValue.NIL;
        });

        builder.add(FUNC_APPEND_TRANSLATABLE, args -> {
            String key = args.nextString();



            obj.append(Component.translatable(key));
            return LuaValue.NIL;
        });

    }

    @Override
    public MutableComponent toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        int ptr = table.get(PROP_POINTER).toint();
        return script.getUnserializableData(ptr, MutableComponent.class);
    }

    @Override
    public String getArgtypeString() {
        return "TextComponent";
    }
}
