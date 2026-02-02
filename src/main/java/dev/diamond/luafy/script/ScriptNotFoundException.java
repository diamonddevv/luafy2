package dev.diamond.luafy.script;

import net.minecraft.resources.Identifier;

public class ScriptNotFoundException extends RuntimeException {
    public ScriptNotFoundException(Identifier id) {
        super("The script with id " + id.toString() + " was not found! (It may not exist or it may be spelled wrong)");
    }
}
