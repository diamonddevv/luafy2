package dev.diamond.luafy.resource;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.LuaScript;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ScriptResourceLoader implements SimpleSynchronousResourceReloadListener {

    private static final String PATH = "luafy/scripts";
    private static final String LIBRARY_PATH = "lib";
    private static final String EXT = ".lua";

    @Override
    public @NotNull Identifier getFabricId() {
        return Luafy.id("scripts");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Luafy.SCRIPT_MANAGER.clearScriptsCache();
        int count = 0;

        for (Identifier loc : manager.listResources(PATH, p -> true).keySet()) {
            if (manager.getResource(loc).isPresent()) {
                try (InputStream stream = manager.getResource(loc).get().open()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);


                    Identifier id = idFromBadPath(loc.getPath(), loc.getNamespace());


                    // get script
                    Luafy.SCRIPT_MANAGER.loadScript(id, new LuaScript(s), id.getPath().startsWith(LIBRARY_PATH + "/"));
                    count += 1;

                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading script at {}", loc.toString(), e);
                }
            }
        }

        Luafy.LOGGER.info("Loaded {} scripts", count);
    }

    public static Identifier idFromBadPath(String badPath, String namespace) {
        String fixedPath = badPath.substring(PATH.length() + 1, badPath.length() - EXT.length());
        return Identifier.fromNamespaceAndPath(namespace, fixedPath);
    }
}
