package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.enumeration.Note;
import dev.diamond.luafy.script.enumeration.Instrument;
import dev.diamond.luafy.script.enumeration.ScriptEnum;
import dev.diamond.luafy.script.enumeration.TextComponentColor;
import net.minecraft.core.Registry;

public class ScriptEnums {

    public static ScriptEnum<Note> NOTE = new ScriptEnum<>(Note.class);
    public static ScriptEnum<Instrument> INSTRUMENT = new ScriptEnum<>(Instrument.class);
    public static ScriptEnum<TextComponentColor> TEXT_COMPONENT_COLOR = new ScriptEnum<>(TextComponentColor.class);

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_ENUMS, Luafy.id("note"), NOTE);
        Registry.register(LuafyRegistries.SCRIPT_ENUMS, Luafy.id("instrument"), INSTRUMENT);
        Registry.register(LuafyRegistries.SCRIPT_ENUMS, Luafy.id("text_component_color"), TEXT_COMPONENT_COLOR);
    }
}
