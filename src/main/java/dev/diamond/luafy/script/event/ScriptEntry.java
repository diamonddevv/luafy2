package dev.diamond.luafy.script.event;

import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.script.ScriptExecutionResult;
import java.util.concurrent.Future;
import net.minecraft.resources.Identifier;

public class ScriptEntry {
    public Identifier id;
    public boolean awaitCompletion;

    private Future<ScriptExecutionResult> lastResult;

    public ScriptEntry(ScriptEntry.Bean bean) {
        this.id = Identifier.parse(bean.id);
        this.awaitCompletion = bean.awaitCompletion;
    }

    public boolean canExecute() {
        if (awaitCompletion && lastResult != null) {
            return lastResult.isDone();
        } else {
            return true;
        }
    }

    public void setLastResult(Future<ScriptExecutionResult> result) {
        this.lastResult = result;
    }

    public static class Bean {
        @SerializedName("id")
        public String id;

        @SerializedName("await_completion")
        public boolean awaitCompletion;
    }
}
