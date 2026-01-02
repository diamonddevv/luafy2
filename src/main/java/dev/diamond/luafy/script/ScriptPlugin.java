package dev.diamond.luafy.script;

import java.util.function.Consumer;

public class ScriptPlugin {

    private final Consumer<LuaScript> applier;

    public ScriptPlugin(Consumer<LuaScript> applier) {
        this.applier = applier;
    }

    public void apply(LuaScript script) {
        applier.accept(script);
    }
}
