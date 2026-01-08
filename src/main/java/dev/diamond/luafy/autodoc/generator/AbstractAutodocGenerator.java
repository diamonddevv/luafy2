package dev.diamond.luafy.autodoc.generator;

import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.ApiScriptPlugin;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.api.AbstractScriptApi;
import dev.diamond.luafy.script.enumeration.ScriptEnum;
import dev.diamond.luafy.script.event.ScriptEvent;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

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


    public String buildOutput(File file) {

        // write doc
        StringBuilder doc = new StringBuilder();

        addFileHeader(doc);


        startRegion(doc, REGION_SCRIPT_ENUM);
        LuafyRegistries.SCRIPT_ENUMS.forEach(obj -> {
            Identifier id = LuafyRegistries.SCRIPT_ENUMS.getId(obj);
            assert id != null;
            addEnum(doc, obj);
        });
        endRegion(doc, REGION_SCRIPT_ENUM);

        startRegion(doc, REGION_SCRIPT_OBJECT);
        LuafyRegistries.SCRIPT_OBJECTS.forEach(obj -> {
            Identifier id = LuafyRegistries.SCRIPT_OBJECTS.getId(obj);
            assert id != null;
            addScriptObject(doc, obj);
        });
        endRegion(doc, REGION_SCRIPT_OBJECT);

        startRegion(doc, REGION_SCRIPT_API);
        LuafyRegistries.SCRIPT_PLUGINS.forEach(obj -> {
            if (obj instanceof ApiScriptPlugin<?> api) {
                Identifier id = LuafyRegistries.SCRIPT_PLUGINS.getId(api);
                assert id != null;

                ApiScriptPlugin.DocInfo b = api.generatePopulatedFunctionList();

                addScriptApi(doc, b);
            }
        });
        endRegion(doc, REGION_SCRIPT_API);

        startRegion(doc, REGION_SCRIPT_EVENT);
        LuafyRegistries.SCRIPT_EVENTS.forEach(obj -> {
            Identifier id = LuafyRegistries.SCRIPT_EVENTS.getId(obj);
            assert id != null;
            addScriptEvent(doc, obj);
        });
        endRegion(doc, REGION_SCRIPT_EVENT);


        // write file
        file.getParentFile().mkdirs(); // make parent directories if needed
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(doc.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write autodoc file: " + e);
        }
        return file.getPath();
    }


    public static File getDefaultFile(AbstractAutodocGenerator generator) {
        Path rootpath = FabricLoader.getInstance().getGameDir().resolve("luafy_autodocs");
        String fn = "autodoc." + generator.fileExtension;
        return new File(rootpath.toString(), fn);
    }
}
