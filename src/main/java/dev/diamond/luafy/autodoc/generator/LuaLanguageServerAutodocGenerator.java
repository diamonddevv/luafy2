package dev.diamond.luafy.autodoc.generator;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.script.ApiScriptPlugin;
import dev.diamond.luafy.script.enumeration.ScriptEnum;
import dev.diamond.luafy.script.event.ScriptEvent;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.fabricmc.loader.api.FabricLoader;

import java.time.LocalDateTime;

public class LuaLanguageServerAutodocGenerator extends AbstractAutodocGenerator {
    public LuaLanguageServerAutodocGenerator() {
        super("lua");
    }

    @Override
    public void addFileHeader(StringBuilder doc) {
        doc.append(String.format("""
                ---@meta luafyautodoc
                
                --#region Information
                -- GENERATED AUTODOC
                -- Generated: %s
                -- Luafy Version: %s
                -- Format: Lua LS library file
                --#endregion
                
                ---@type table
                ctx = {}
                
                """, LocalDateTime.now(),
                FabricLoader.getInstance().getModContainer(Luafy.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString())
        );
    }

    @Override
    public void addScriptObject(StringBuilder doc, AbstractScriptObject<?> scriptObject) {
        doc.append("--- ").append(scriptObject.getDesc()).append("\n");
        doc.append("---@class ").append(scriptObject.getArgtypeString());
        if (scriptObject.getParentType().isPresent()) {
            doc.append(": ").append(scriptObject.getParentType().get().getArgtypeString());
        }
        doc.append("\n");
        for (var field : scriptObject.getProperties()) {
            doc.append("---@field ");
            doc.append(field.name()).append(" ");
            doc.append(field.type().getArgtypeString()).append(" ");
            doc.append(field.desc()).append("\n");
        }
        doc.append("local ").append(scriptObject.getArgtypeString()).append(" = {}\n\n");

        for (var function : scriptObject.getFunctions()) {
            doc.append("--- ").append(function.funcDesc()).append("\n");
            for (var arg : function.args()) {
                doc.append("---@param ");
                doc.append(arg.argName()).append(" ");
                doc.append(arg.argType().getArgtypeString()).append(" ");
                doc.append(arg.argDesc()).append("\n");
            }
            doc.append("---@return ");
            doc.append(function.returnType().getArgtypeString()).append("\n");

            doc.append("function ");
            doc.append(scriptObject.getArgtypeString()).append(".").append(function.funcName());
            doc.append("(");
            for (int i = 0; i < function.args().size(); i++) {
                doc.append(function.args().get(i).argName());
                if (i < function.args().size() - 1) {
                    doc.append(", ");
                }
            }
            doc.append(") end\n\n");
        }

        doc.append("\n");
    }

    @Override
    public void addScriptApi(StringBuilder doc, ApiScriptPlugin.DocInfo api) {
        doc.append(api.name()).append(" = {}\n\n");

        for (var function : api.builder().getDocumentation()) {
            doc.append("--- ").append(function.funcDesc()).append("\n");
            for (var arg : function.args()) {
                doc.append("---@param ");
                doc.append(arg.argName()).append(" ");
                doc.append(arg.argType().getArgtypeString()).append(" ");
                doc.append(arg.argDesc()).append("\n");
            }
            doc.append("---@return ");
            doc.append(function.returnType().getArgtypeString()).append("\n");

            doc.append("function ");
            doc.append(api.name()).append(".").append(function.funcName());
            doc.append("(");
            for (int i = 0; i < function.args().size(); i++) {
                doc.append(function.args().get(i).argName());
                if (i < function.args().size() - 1) {
                    doc.append(", ");
                }
            }
            doc.append(") end\n\n");
        }
    }

    @Override
    public void addScriptEvent(StringBuilder doc, ScriptEvent<?> event) {
        addComment(doc, LuafyRegistries.SCRIPT_EVENTS.getId(event) + " ; this generator does not currently provide additional information.\n");
    }

    @Override
    public void addEnum(StringBuilder doc, ScriptEnum<?> e) {
        doc.append("---@enum (key) ").append(e.getArgtypeString()).append("\n");
        doc.append(e.getArgtypeString()).append(" = {\n");
        for (var key : e.getEnumKeys()) {
            doc.append("\t").append(key).append(" = \"").append(key).append("\",\n");
        }
        doc.append("}\n\n");
    }

    @Override
    public void addComment(StringBuilder doc, String comment) {
        doc.append("-- ").append(comment).append("\n");
    }

    @Override
    public void startRegion(StringBuilder doc, String regionTitle) {
        doc.append("--#region ").append(regionTitle).append("\n\n");
    }

    @Override
    public void endRegion(StringBuilder doc, String regionTitle) {
        doc.append("--#endregion\n\n");
    }
}
