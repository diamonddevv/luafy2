package dev.diamond.luafy.script;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.registry.LuafyRegistries;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ScriptEventResourceLoader implements SimpleSynchronousResourceReloadListener {

    private static final String PATH = "luafy/events";
    private static final String EXT = ".json";

    @Override
    public @NotNull Identifier getFabricId() {
        return Luafy.id("events");
    }

    @Override
    public void reload(ResourceManager manager) {
        Gson gson = new Gson();
        Luafy.SCRIPT_MANAGER.clearScriptEventsCaches();
        int count = 0;

        for (Identifier loc : manager.findResources(PATH, p -> true).keySet()) {
            if (manager.getResource(loc).isPresent()) {
                try (InputStream stream = manager.getResource(loc).get().getInputStream()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);
                    Bean data = gson.fromJson(s, Bean.class);


                    String fixedPath = loc.getPath().substring(PATH.length() + 1, loc.getPath().length() - EXT.length());
                    Identifier id = Identifier.of(loc.getNamespace(), fixedPath);

                    if (LuafyRegistries.SCRIPT_EVENTS.containsId(id)) {
                        var event = LuafyRegistries.SCRIPT_EVENTS.get(id);
                        assert event != null; // we checked it exists so this is fineeeee

                        event.register(data.identifiers.stream().map(Identifier::of).collect(Collectors.toSet()));

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
        public ArrayList<String> identifiers;
    }
}
