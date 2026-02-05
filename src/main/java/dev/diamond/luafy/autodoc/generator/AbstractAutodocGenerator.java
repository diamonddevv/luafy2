package dev.diamond.luafy.autodoc.generator;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.autodoc.ArglistBuilder;
import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.autodoc.FunctionDocInfo;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.ApiScriptPlugin;
import dev.diamond.luafy.script.type.enumeration.ScriptEnum;
import dev.diamond.luafy.script.event.ScriptEvent;
import dev.diamond.luafy.script.type.object.AbstractScriptObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractAutodocGenerator {
    public final String fileExtension;

    private static final String REGION_SCRIPT_ENUM = "Enums";
    private static final String REGION_SCRIPT_OBJECT = "Script Object";
    private static final String REGION_SCRIPT_API = "Script Api";
    private static final String REGION_SCRIPT_EVENT = "Script Event";
    private static final String REGION_DEFAULT_OVERRIDES = "Default Overrides";

    public AbstractAutodocGenerator(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public abstract void addFileHeader(StringBuilder doc);
    public abstract void addScriptObject(StringBuilder doc, AbstractScriptObject<?> scriptObject);
    public abstract void addScriptApi(StringBuilder doc, ApiScriptPlugin.DocInfo api);
    public abstract void addScriptEvent(StringBuilder doc, ScriptEvent<?> event);
    public abstract void addEnum(StringBuilder doc, ScriptEnum<?> e);
    public abstract void addComment(StringBuilder doc, String comment);
    public abstract void startRegion(StringBuilder doc, String regionTitle);
    public abstract void endRegion(StringBuilder doc, String regionTitle);
    public abstract void addExtraOverriddenFunction(StringBuilder doc, FunctionDocInfo function);

    public String buildOutput(File file) {
        Luafy.LOGGER.info("Generating autodoc (Generator class: '{}')..", this.getClass().getSimpleName());
        long time = System.currentTimeMillis();

        // write doc
        StringBuilder doc = new StringBuilder();

        addFileHeader(doc);

        startRegion(doc, REGION_DEFAULT_OVERRIDES);
        addExtraOverriddenFunction(doc, new FunctionDocInfo(
                "require",
                "Load a module. To use default Lua behaviour, ignore the second argument. " +
                        "To load a module from within a datapack, add the namespace of the target " +
                        "datapack as the second argument.",
                new ArglistBuilder()
                        .add("modname", Argtypes.STRING, "Module name to load.")
                        .add("namespace", Argtypes.maybe(Argtypes.STRING), "Namespace of datapack to load from. Leave blank to use default Lua require behaviour, which probably won't work.")
                        .build(),
                Argtypes.TABLE
        )); // require
        Luafy.LOGGER.info("Added default override functions.");
        endRegion(doc, REGION_DEFAULT_OVERRIDES);


        startRegion(doc, REGION_SCRIPT_ENUM);
        LuafyRegistries.SCRIPT_ENUMS.forEach(obj -> {
            Identifier id = LuafyRegistries.SCRIPT_ENUMS.getKey(obj);
            assert id != null;
            addEnum(doc, obj);
            Luafy.LOGGER.info("Added enum {}", id);
        });
        endRegion(doc, REGION_SCRIPT_ENUM);

        startRegion(doc, REGION_SCRIPT_OBJECT);
        LuafyRegistries.SCRIPT_OBJECTS.forEach(obj -> {
            Identifier id = LuafyRegistries.SCRIPT_OBJECTS.getKey(obj);
            assert id != null;
            addScriptObject(doc, obj);
            Luafy.LOGGER.info("Added object {}", id);
        });
        endRegion(doc, REGION_SCRIPT_OBJECT);

        startRegion(doc, REGION_SCRIPT_API);
        LuafyRegistries.SCRIPT_PLUGINS.forEach(obj -> {
            if (obj instanceof ApiScriptPlugin<?> api) {
                Identifier id = LuafyRegistries.SCRIPT_PLUGINS.getKey(api);
                assert id != null;

                ApiScriptPlugin.DocInfo b = api.generatePopulatedFunctionList();

                addScriptApi(doc, b);
                Luafy.LOGGER.info("Added api {}", id);
            }
        });
        endRegion(doc, REGION_SCRIPT_API);

        startRegion(doc, REGION_SCRIPT_EVENT);
        LuafyRegistries.SCRIPT_EVENTS.forEach(obj -> {
            Identifier id = LuafyRegistries.SCRIPT_EVENTS.getKey(obj);
            assert id != null;
            addScriptEvent(doc, obj);
            Luafy.LOGGER.info("Added event {}", id);
        });
        endRegion(doc, REGION_SCRIPT_EVENT);


        // write file
        file.getParentFile().mkdirs(); // make parent directories if needed
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(doc.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write autodoc file: " + e);
        }

        String path = file.getPath();
        Luafy.LOGGER.info("Generated autodoc (Generator class: '{}') at {}. (took {}ms)", this.getClass().getSimpleName(), path, System.currentTimeMillis() - time);
        return path;
    }


    public static File getDefaultFile(AbstractAutodocGenerator generator) {
        Path rootpath = FabricLoader.getInstance().getGameDir().resolve("luafy_autodocs");
        String fn = "autodoc." + generator.fileExtension;
        return new File(rootpath.toString(), fn);
    }
}
