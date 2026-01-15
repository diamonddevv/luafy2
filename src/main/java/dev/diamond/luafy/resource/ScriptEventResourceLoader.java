package dev.diamond.luafy.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.event.ScriptEntry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ScriptEventResourceLoader implements SimpleSynchronousResourceReloadListener {

    private static final String PATH = "luafy/events";
    private static final String EXT = ".json";

    @Override
    public @NotNull Identifier getFabricId() {
        return Luafy.id("events");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Gson gson = new Gson();
        Luafy.SCRIPT_MANAGER.clearScriptEventsCaches();
        int count = 0;

        for (Identifier loc : manager.listResources(PATH, p -> true).keySet()) {
            if (manager.getResource(loc).isPresent()) {
                try (InputStream stream = manager.getResource(loc).get().open()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);
                    Bean data = gson.fromJson(s, Bean.class);


                    String fixedPath = loc.getPath().substring(PATH.length() + 1, loc.getPath().length() - EXT.length());
                    Identifier id = Identifier.fromNamespaceAndPath(loc.getNamespace(), fixedPath);

                    if (LuafyRegistries.SCRIPT_EVENTS.containsKey(id)) {
                        var event = LuafyRegistries.SCRIPT_EVENTS.getValue(id);
                        assert event != null; // we checked it exists so this is fineeeee


                        ArrayList<ScriptEntry> entries = new ArrayList<>();
                        for (var e : data.entries) {

                            if (e.isJsonPrimitive()) {
                                String stringId = e.getAsString();
                                e = new JsonObject();
                                ((JsonObject) e).addProperty("id", stringId);
                            }

                            entries.add(new ScriptEntry(gson.fromJson(e.getAsJsonObject(), ScriptEntry.Bean.class)));
                        }

                        event.register(entries);

                        count += 1;
                    }

                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading event list at {}", loc.toString(), e);
                }
            }
        }

        Luafy.LOGGER.info("Loaded {} event lists", count);
    }


    private static class Bean {
        @SerializedName("scripts")
        public ArrayList<JsonElement> entries;
    }

}
