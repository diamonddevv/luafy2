package dev.diamond.luafy.script;

import org.luaj.vm2.LuaValue;

public class ScriptExecutionResult {
    private final LuaValue value;
    private final String error;

    public ScriptExecutionResult(LuaValue value, String error) {
        this.value = value;
        this.error = error;
    }

    public boolean success() {
        return error.isBlank();
    }

    public String getError() {
        return this.error;
    }

    public LuaValue getResult() {
        return this.value;
    }
}
