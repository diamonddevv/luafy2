package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.ArgtypeStrings;
import dev.diamond.luafy.lua.LuaTableBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;

public class ModScriptObject extends AbstractScriptObject<ModContainer> {

    public static final String PROP_MODID = "modid";
    public static final String PROP_VERSION = "version";

    public ModScriptObject() {
        super("An object representing a mod installed on the server.", doc -> {
            doc.addProperty(PROP_MODID, ArgtypeStrings.STRING, "The id of this mod.");
            doc.addProperty(PROP_VERSION, ArgtypeStrings.STRING, "The version of the mod currently installed.");
        });
    }

    @Override
    public void toTable(ModContainer obj, LuaTableBuilder builder) {
        builder.add(PROP_MODID, obj.getMetadata().getId());
        builder.add(PROP_VERSION, obj.getMetadata().getVersion().getFriendlyString());

        makeReadonly(builder);
    }

    @Override
    public ModContainer toThing(LuaTable table, ServerCommandSource src) {
        return FabricLoader.getInstance().getModContainer(table.get(PROP_MODID).tojstring()).orElseThrow();
    }

    @Override
    public String getArgTypeString() {
        return "mod";
    }
}
