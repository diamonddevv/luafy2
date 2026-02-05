package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.type.Argtypes;
import net.minecraft.core.Registry;

public class ScriptArgtypes {

    // technically even things that are registered might not be serializable
    // lol

    public static void registerAll() {

        Registry.register(LuafyRegistries.ARGTYPES, Luafy.id_luaj("nil"), Argtypes.NIL);
        Registry.register(LuafyRegistries.ARGTYPES, Luafy.id_luaj("boolean"), Argtypes.BOOLEAN);
        Registry.register(LuafyRegistries.ARGTYPES, Luafy.id_luaj("number"), Argtypes.NUMBER);
        Registry.register(LuafyRegistries.ARGTYPES, Luafy.id_luaj("string"), Argtypes.STRING);
        Registry.register(LuafyRegistries.ARGTYPES, Luafy.id_luaj("any"), Argtypes.VALUE);
        Registry.register(LuafyRegistries.ARGTYPES, Luafy.id_luaj("integer"), Argtypes.INTEGER);
        Registry.register(LuafyRegistries.ARGTYPES, Luafy.id_luaj("table"), Argtypes.TABLE);


    }
}
