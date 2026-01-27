package dev.diamond.luafy.script.object.game;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.ScriptFunction;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

public class TextComponentScriptObject extends AbstractScriptObject<Component> {

    public static final String PROP_POINTER = "_ptr";

    public static final String FUNC_SERIALISE = "serialise";
    public static final String FUNC_COLOR = "color";
    public static final String FUNC_FONT = "font";
    public static final String FUNC_EMBOLDEN = "bold";
    public static final String FUNC_ITALICISE = "italic";
    public static final String FUNC_UNDERLINE = "underline";
    public static final String FUNC_STRIKETHROUGH = "strikethrough";
    public static final String FUNC_OBFUSCATE = "obfuscated";


    public TextComponentScriptObject() {
        super("Text component.", doc -> {
            doc.addFunction(FUNC_SERIALISE, "Serialises this text component to a JSON string.", args -> {}, Argtypes.STRING);
            doc.addFunction(FUNC_COLOR,         "Sets the text's color.",       args -> {}, Argtypes.NIL);
            doc.addFunction(FUNC_FONT,          "Sets the text's font.",        args -> args.add("font", Argtypes.STRING, "Identifier of a Font to use."),          Argtypes.NIL);
            doc.addFunction(FUNC_EMBOLDEN,      "Emboldens the text.",          args -> args.add("flag", Argtypes.BOOLEAN, "If true, emboldens the text."),         Argtypes.NIL);
            doc.addFunction(FUNC_ITALICISE,     "Italicises the text.",         args -> args.add("flag", Argtypes.BOOLEAN, "If true, italicises the text."),        Argtypes.NIL);
            doc.addFunction(FUNC_UNDERLINE,     "Underlines the text.",         args -> args.add("flag", Argtypes.BOOLEAN, "If true, underlines the text."),        Argtypes.NIL);
            doc.addFunction(FUNC_STRIKETHROUGH, "Strikes through the text.",    args -> args.add("flag", Argtypes.BOOLEAN, "If true, strikes through the text."),   Argtypes.NIL);
            doc.addFunction(FUNC_OBFUSCATE,     "Obfuscates the text.",         args -> args.add("flag", Argtypes.BOOLEAN, "If true, obfuscates the text."),        Argtypes.NIL);



        });
    }

    public static void addStaticTextComponentBuilderMethods(FunctionListBuilder builder, LuaScript script) {
        builder.add("literal_text", args -> {
            return LuaTableBuilder.provide(ScriptObjects.TEXT_COMPONENT, Component.literal(args.nextString()), script);
        }, "Creates a literal text component.", args -> {
            args.add("literal", Argtypes.STRING, "Literal text.");
        }, ScriptObjects.TEXT_COMPONENT);

        builder.add("translatable_text", args -> {
            String key = args.nextString();
            ArrayList<Component> components = args.nextArray(v ->
                    args.getScriptObject(ScriptObjects.TEXT_COMPONENT, v, script.getSource(), script), new ArrayList<>());

            return LuaTableBuilder.provide(
                    ScriptObjects.TEXT_COMPONENT,
                    MutableComponent.create(
                            new TranslatableContents(
                                    key, null, components.toArray()
                            )
                    ), script
            );
        }, "Creates a translatable text component.", args -> {
            args.add("translatable", Argtypes.STRING, "Translation key");
            args.add("components", Argtypes.maybe(Argtypes.array(ScriptObjects.TEXT_COMPONENT)), "Optional list of components to use as placeholders.");
        }, ScriptObjects.TEXT_COMPONENT);

        builder.add("player_sprite_text", args -> {
            ServerPlayer player = args.nextScriptObject(ScriptObjects.PLAYER, script.getSource(), script);
            return LuaTableBuilder.provide(ScriptObjects.TEXT_COMPONENT, Component.object(
                    new PlayerSprite(ResolvableProfile.createResolved(player.getGameProfile()), true)
            ), script);
        }, "Creates a player sprite text component.", args -> {
            args.add("player", ScriptObjects.PLAYER, "Player to use.");
        }, ScriptObjects.TEXT_COMPONENT);

        builder.add("atlas_sprite_text", args -> {
            Identifier atlas = Identifier.parse(args.nextString());
            Identifier sprite = Identifier.parse(args.nextString());
            return LuaTableBuilder.provide(ScriptObjects.TEXT_COMPONENT, Component.object(
                    new AtlasSprite(atlas, sprite)
            ), script);
        }, "Creates a atlas-source sprite text component.", args -> {
            args.add("atlas", Argtypes.STRING, "Atlas to use.");
            args.add("sprite", Argtypes.STRING, "Sprite to use.");
        }, ScriptObjects.TEXT_COMPONENT);

        builder.add("compound_text", args -> {
            MutableComponent compound = Component.empty();
            ArrayList<Component> components = args.nextArray(v -> args.getScriptObject(ScriptObjects.TEXT_COMPONENT, v, script.getSource(), script));

            for (var comp : components) {
                compound.append(comp);
            }

            return LuaTableBuilder.provide(ScriptObjects.TEXT_COMPONENT, compound, script);
        }, "Creates a text component by concatenating several elements together.", args -> {
            args.add("elements", Argtypes.array(ScriptObjects.TEXT_COMPONENT), "Elements to concatenate together.");
        }, ScriptObjects.TEXT_COMPONENT);
    }

    @Override
    public void toTable(Component obj, LuaTableBuilder builder, LuaScript script) {

        // i probably could serialise this, but i think this would be faster
        builder.add(PROP_POINTER, script.addUnserializableData(obj));


        builder.add(FUNC_SERIALISE, args -> {
            var json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, obj);
            return LuaValue.valueOf(json.getOrThrow().toString());
        });



    }

    @Override
    public Component toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        int ptr = table.get(PROP_POINTER).toint();
        return script.getUnserializableData(ptr, Component.class);
    }

    @Override
    public String getArgtypeString() {
        return "TextComponent";
    }
}
