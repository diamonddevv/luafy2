package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.type.enumeration.Note;
import dev.diamond.luafy.script.type.enumeration.Instrument;
import dev.diamond.luafy.script.type.enumeration.ScriptEnum;
import dev.diamond.luafy.script.type.enumeration.TextComponentColor;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ScriptEnums {

    public static ScriptEnum<Note> NOTE = new ScriptEnum<>(Note.class);
    public static ScriptEnum<Instrument> INSTRUMENT = new ScriptEnum<>(Instrument.class);
    public static ScriptEnum<TextComponentColor> TEXT_COMPONENT_COLOR = new ScriptEnum<>(TextComponentColor.class);

    public static void registerAll() {
        register(Luafy.id("note"), NOTE);
        register(Luafy.id("instrument"), INSTRUMENT);
        register(Luafy.id("text_component_color"), TEXT_COMPONENT_COLOR);
    }

    public static void register(Identifier id, ScriptEnum<?> scriptEnum) {
        Registry.register(LuafyRegistries.SCRIPT_ENUMS, id, scriptEnum);
        Registry.register(LuafyRegistries.STRING_ALIASES, id, scriptEnum);
        Registry.register(LuafyRegistries.ARGTYPES, id, scriptEnum);
    }
}
