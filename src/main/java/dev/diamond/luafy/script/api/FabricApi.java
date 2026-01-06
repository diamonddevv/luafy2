package dev.diamond.luafy.script.api;

import dev.diamond.luafy.autodoc.ArgtypeStrings;
import dev.diamond.luafy.autodoc.FunctionListBuilder;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.lua.MetamethodImpl;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.Optional;

public class FabricApi extends AbstractScriptApi {
    public FabricApi(LuaScript script) {
        super("fabric", script);
    }

    @Override
    public void addFunctions(FunctionListBuilder builder) {
        builder.add("get_version", args -> {
            return LuaString.valueOf(FabricLoaderImpl.VERSION);
        }, "Returns the current Fabric version string.", args -> {}, ArgtypeStrings.STRING);

        builder.add("has_mod", args -> {
            String modid = MetamethodImpl.tostring(args.arg1());
            Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modid);
            return LuaBoolean.valueOf(mod.isPresent());
        }, "Returns true if the specified mod exists.", args -> {
            args.add("modid", ArgtypeStrings.STRING, "A mod id.");
        }, ArgtypeStrings.BOOLEAN);

        builder.add("get_mod", args -> {
            String modid = MetamethodImpl.tostring(args.arg1());

            Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modid);
            if (mod.isPresent()) {
                return LuaTableBuilder.provide(b -> ScriptObjects.MOD.toTable(mod.get(), b));
            } else {
                return LuaValue.NIL;
            }
        }, "Returns an object representing an installed mod. Returns nil if the specified mod does not exist.", args -> {
            args.add("modid", ArgtypeStrings.STRING, "A mod id.");
        }, ScriptObjects.MOD.getArgTypeString());

        builder.add("get_mods", args -> {
            return LuaTableBuilder.ofArrayStrings(FabricLoader.getInstance().getAllMods().stream().map(
                    mod -> mod.getMetadata().getId()
            ).toList());
        }, "Returns a list of all the mods that are installed.", args -> {}, ArgtypeStrings.LIST);
    }
}
