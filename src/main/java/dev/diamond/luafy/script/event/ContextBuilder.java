package dev.diamond.luafy.script.event;

import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;


@FunctionalInterface
public interface ContextBuilder<T> {
    void build(LuaTableBuilder b, T ctx, LuaScript script);
}
