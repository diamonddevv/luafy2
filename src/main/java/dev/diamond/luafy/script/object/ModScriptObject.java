package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.LuaTable;

public class ModScriptObject extends AbstractScriptObject<ModContainer> {

    public static final String PROP_MODID = "modid";
    public static final String PROP_VERSION = "version";

    public ModScriptObject() {
        super("An object representing a mod installed on the server.", doc -> {
            doc.addProperty(PROP_MODID, Argtypes.STRING, "The id of this mod.");
            doc.addProperty(PROP_VERSION, Argtypes.STRING, "The version of the mod currently installed.");
        });
    }

    @Override
    public void toTable(ModContainer obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(PROP_MODID, obj.getMetadata().getId());
        builder.add(PROP_VERSION, obj.getMetadata().getVersion().getFriendlyString());

        makeReadonly(builder);
    }

    @Override
    public ModContainer toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return FabricLoader.getInstance().getModContainer(table.get(PROP_MODID).tojstring()).orElseThrow();
    }

    @Override
    public String getArgtypeString() {
        return "Mod";
    }
}
