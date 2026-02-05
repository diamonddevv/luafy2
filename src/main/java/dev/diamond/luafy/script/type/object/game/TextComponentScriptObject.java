package dev.diamond.luafy.script.type.object.game;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.JsonOps;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptEnums;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.type.enumeration.TextComponentColor;
import dev.diamond.luafy.script.type.object.AbstractScriptObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.FontDescription;
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
import java.util.Optional;

public class TextComponentScriptObject extends AbstractScriptObject<MutableComponent> {

    public static final String PROP_POINTER = "_ptr";

    public static final String FUNC_SERIALISE = "serialise";
    public static final String FUNC_COLOR_INT = "color";
    public static final String FUNC_COLOR_PREDEF = "color_predefined";
    public static final String FUNC_FONT = "font";
    public static final String FUNC_EMBOLDEN = "bold";
    public static final String FUNC_ITALICISE = "italic";
    public static final String FUNC_UNDERLINE = "underline";
    public static final String FUNC_STRIKETHROUGH = "strikethrough";
    public static final String FUNC_OBFUSCATE = "obfuscated";


    public TextComponentScriptObject() {
        super("Text component.", doc -> {
            doc.addFunction(FUNC_SERIALISE, "Serialises this text component to a JSON string.", args -> {}, Argtypes.STRING);
            doc.addFunction(FUNC_COLOR_INT,     "Sets the text's color.", args -> args.add("color", Argtypes.INTEGER, "Color as an integer."), ScriptObjects.TEXT_COMPONENT);
            doc.addFunction(FUNC_COLOR_PREDEF,  "Sets the text's color.", args -> args.add("color", ScriptEnums.TEXT_COMPONENT_COLOR, "Predefined color."), ScriptObjects.TEXT_COMPONENT);
            doc.addFunction(FUNC_FONT,          "Sets the text's font.",        args -> args.add("font", Argtypes.STRING, "Identifier of a Font to use."),          ScriptObjects.TEXT_COMPONENT);
            doc.addFunction(FUNC_EMBOLDEN,      "Emboldens the text.",          args -> args.add("flag", Argtypes.BOOLEAN, "If true, emboldens the text."),         ScriptObjects.TEXT_COMPONENT);
            doc.addFunction(FUNC_ITALICISE,     "Italicises the text.",         args -> args.add("flag", Argtypes.BOOLEAN, "If true, italicises the text."),        ScriptObjects.TEXT_COMPONENT);
            doc.addFunction(FUNC_UNDERLINE,     "Underlines the text.",         args -> args.add("flag", Argtypes.BOOLEAN, "If true, underlines the text."),        ScriptObjects.TEXT_COMPONENT);
            doc.addFunction(FUNC_STRIKETHROUGH, "Strikes through the text.",    args -> args.add("flag", Argtypes.BOOLEAN, "If true, strikes through the text."),   ScriptObjects.TEXT_COMPONENT);
            doc.addFunction(FUNC_OBFUSCATE,     "Obfuscates the text.",         args -> args.add("flag", Argtypes.BOOLEAN, "If true, obfuscates the text."),        ScriptObjects.TEXT_COMPONENT);
        });
    }

    public static void addStaticTextComponentBuilderMethods(FunctionListBuilder builder, LuaScript script) {
        builder.add("literal", args -> {
            return LuaTableBuilder.provide(ScriptObjects.TEXT_COMPONENT, Component.literal(args.nextString()), script);
        }, "Creates a literal text component.", args -> {
            args.add("literal", Argtypes.STRING, "Literal text.");
        }, ScriptObjects.TEXT_COMPONENT);

        builder.add("translatable", args -> {
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

        builder.add("player_sprite", args -> {
            ServerPlayer player = args.nextScriptObject(ScriptObjects.PLAYER, script.getSource(), script);
            return LuaTableBuilder.provide(ScriptObjects.TEXT_COMPONENT, Component.object(
                    new PlayerSprite(ResolvableProfile.createResolved(player.getGameProfile()), true)
            ), script);
        }, "Creates a player sprite text component.", args -> {
            args.add("player", ScriptObjects.PLAYER, "Player to use.");
        }, ScriptObjects.TEXT_COMPONENT);

        builder.add("atlas_sprite", args -> {
            Identifier atlas = Identifier.parse(args.nextString());
            Identifier sprite = Identifier.parse(args.nextString());
            return LuaTableBuilder.provide(ScriptObjects.TEXT_COMPONENT, Component.object(
                    new AtlasSprite(atlas, sprite)
            ), script);
        }, "Creates a atlas-source sprite text component.", args -> {
            args.add("atlas", Argtypes.STRING, "Atlas to use.");
            args.add("sprite", Argtypes.STRING, "Sprite to use.");
        }, ScriptObjects.TEXT_COMPONENT);

        builder.add("compound", args -> {
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
    public void toTable(MutableComponent obj, LuaTableBuilder builder, LuaScript script) {

        // i probably could serialise this, but i think this would be faster
        builder.add(PROP_POINTER, script.addUnserializableData(obj));


        builder.add(FUNC_SERIALISE, args -> {
            var json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, obj);
            return LuaValue.valueOf(json.getOrThrow().toString());
        });

        builder.add(FUNC_COLOR_INT, args -> {
            int col = args.nextInt();
            obj.withColor(col);

            return builder.futureSelf();
        });

        builder.add(FUNC_COLOR_PREDEF, args -> {
            TextComponentColor col = args.nextEnumKey(ScriptEnums.TEXT_COMPONENT_COLOR);
            obj.withStyle(col.getColor());

            return builder.futureSelf();
        });

        builder.add(FUNC_FONT, args -> {
            Identifier id = Identifier.parse(args.nextString());
            obj.withStyle(s -> s.withFont(new FontDescription.Resource(id)));

            return builder.futureSelf();
        });

        builder.add(FUNC_EMBOLDEN, args -> {
            boolean flag = args.nextBoolean();
            obj.withStyle(s -> s.withBold(flag));

            return builder.futureSelf();
        });

        builder.add(FUNC_ITALICISE, args -> {
            boolean flag = args.nextBoolean();
            obj.withStyle(s -> s.withItalic(flag));

            return builder.futureSelf();
        });

        builder.add(FUNC_UNDERLINE, args -> {
            boolean flag = args.nextBoolean();
            obj.withStyle(s -> s.withUnderlined(flag));

            return builder.futureSelf();
        });

        builder.add(FUNC_STRIKETHROUGH, args -> {
            boolean flag = args.nextBoolean();
            obj.withStyle(s -> s.withStrikethrough(flag));

            return builder.futureSelf();
        });

        builder.add(FUNC_OBFUSCATE, args -> {
            boolean flag = args.nextBoolean();
            obj.withStyle(s -> s.withObfuscated(flag));

            return builder.futureSelf();
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

    @Override
    public Optional<ArgumentType<?>> getCommandArgumentType(CommandBuildContext ctx) {
        return Optional.of(ComponentArgument.textComponent(ctx));
    }

    @Override
    public Optional<LuaTable> parseCommandToLua(CommandContext<CommandSourceStack> cmdCtx, String argName, LuaScript script) {
        return Optional.of(
                provideTable(
                        (MutableComponent) ComponentArgument.getRawComponent(cmdCtx, argName), script
                )
        );
    }

    @Override
    public Optional<SuggestionProvider<CommandSourceStack>> suggest() {
        return Optional.empty();
    }
}
