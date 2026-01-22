package dev.diamond.luafy.script.object;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandSourceStack;
import org.luaj.vm2.LuaTable;

public class ModScriptObject extends AbstractScriptObject<ModContainer> {

    public static final String FUNC_MODID = "get_mod_id";
    public static final String FUNC_VERSION = "get_version";

    public ModScriptObject() {
        super("An object representing a mod installed on the server.", doc -> {
            doc.addFunction(FUNC_MODID, "Gets the id of this mod.", args -> {},  Argtypes.STRING);
            doc.addFunction(FUNC_VERSION, "The version of the mod currently installed.", args -> {},  Argtypes.STRING);
        });
    }

    @Override
    public void toTable(ModContainer obj, LuaTableBuilder builder, LuaScript script) {
        builder.add(FUNC_MODID, args -> obj.getMetadata().getId());
        builder.add(FUNC_VERSION, args -> obj.getMetadata().getVersion().getFriendlyString());

        makeReadonly(builder);
    }

    @Override
    public ModContainer toThing(LuaTable table, CommandSourceStack src, LuaScript script) {
        return FabricLoader.getInstance().getModContainer(table.get(FUNC_MODID).tojstring()).orElseThrow();
    }

    @Override
    public Class<ModContainer> getType() {
        return ModContainer.class;
    }

    @Override
    public String getArgtypeString() {
        return "Mod";
    }
}

