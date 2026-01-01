package dev.diamond.luafy.script;

import dev.diamond.luafy.Luafy;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ScriptResourceLoader implements SimpleSynchronousResourceReloadListener {

    private static final String PATH = "luafy/scripts";
    private static final String EXT = ".lua";

    @Override
    public @NotNull Identifier getFabricId() {
        return Luafy.id("scripts");
    }

    @Override
    public void reload(ResourceManager manager) {
        Luafy.SCRIPT_MANAGER.clearScriptsCache();
        int count = 0;

        for (Identifier loc : manager.findResources(PATH, p -> true).keySet()) {
            if (manager.getResource(loc).isPresent()) {
                try (InputStream stream = manager.getResource(loc).get().getInputStream()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);


                    String fixedPath = loc.getPath().substring(PATH.length() + 1, loc.getPath().length() - EXT.length());
                    Identifier id = Identifier.of(loc.getNamespace(), fixedPath);

                    // get script
                    Luafy.SCRIPT_MANAGER.loadScript(id, new LuaScript(s));
                    count += 1;

                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading script at {}", loc.toString(), e);
                }
            }
        }

        Luafy.LOGGER.info("Loaded {} scripts", count);
    }
}
