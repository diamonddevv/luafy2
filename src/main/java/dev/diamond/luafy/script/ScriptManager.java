package dev.diamond.luafy.script;

import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.event.ScriptEvent;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ScriptManager {


    private final HashMap<Identifier, LuaScript> scripts;

    public ScriptManager() {
        this.scripts = new HashMap<>();
    }

    public void loadScript(Identifier id, LuaScript script) {
        this.scripts.put(id, script);
    }

    public LuaScript get(Identifier id) {
        return this.scripts.get(id);
    }

    public boolean has(Identifier id) {
        return this.scripts.containsKey(id);
    }

    public void clearScriptsCache() {
        this.scripts.clear();
    }

    public Collection<String> getScriptIdStrings() {
        return scripts.keySet().stream().map(Identifier::toString).collect(Collectors.toSet());
    }

    public void clearScriptEventsCaches() {
        for (ScriptEvent event : LuafyRegistries.SCRIPT_EVENTS) {
            event.clear();
        }
    }
}
