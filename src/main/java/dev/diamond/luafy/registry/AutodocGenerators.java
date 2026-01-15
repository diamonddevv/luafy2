package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.autodoc.generator.LuaLanguageServerAutodocGenerator;
import net.minecraft.core.Registry;

public class AutodocGenerators {


    public static final LuaLanguageServerAutodocGenerator LUA_LS = new LuaLanguageServerAutodocGenerator();

    public static void registerAll() {
        Registry.register(LuafyRegistries.AUTODOC_GENERATORS, Luafy.id("lua_language_server"), LUA_LS);
    }
}
