package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.type.Argtypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ScriptArgtypes {

    // technically even things that are registered might not be serializable
    // lol

    public static void registerAll() {

        Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, Luafy.id_luaj("nil"), Argtypes.NIL);
        Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, Luafy.id_luaj("boolean"), Argtypes.BOOLEAN);
        Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, Luafy.id_luaj("number"), Argtypes.NUMBER);
        Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, Luafy.id_luaj("string"), Argtypes.STRING);
        Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, Luafy.id_luaj("any"), Argtypes.VALUE);
        Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, Luafy.id_luaj("integer"), Argtypes.INTEGER);
        Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, Luafy.id_luaj("table"), Argtypes.TABLE);

        for (var enumeration : LuafyRegistries.SCRIPT_ENUMS) {
            Identifier id = LuafyRegistries.SCRIPT_ENUMS.getKey(enumeration);
            Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, id, enumeration);
        }

        for (var obj : LuafyRegistries.SCRIPT_OBJECTS) {
            Identifier id = LuafyRegistries.SCRIPT_OBJECTS.getKey(obj);
            Registry.register(LuafyRegistries.SERIALIZABLE_ARGTYPES, id, obj);
        }

    }
}
