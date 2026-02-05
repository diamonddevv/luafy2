package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.type.Argtypes;
import dev.diamond.luafy.autodoc.ScriptApiBuilder;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.script.LuaScript;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.CommandStorage;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class NbtStorageApi extends AbstractScriptApi {
    public NbtStorageApi(LuaScript script) {
        super("nbtstorage", script);
    }

    @Override
    public void addFunctions(ScriptApiBuilder api) {
        api.addGroupless(builder -> {

           builder.add("read", args -> {
               Identifier id = args.nextMap(args::getString, Identifier::parse);
               return LuaTableBuilder.fromNbtCompound(getStorage(script).get(id));
           }, "Reads abstract NBT data from storage.", args -> {
               args.add("id", Argtypes.STRING, "Storage id to read.");
           }, Argtypes.TABLE);

           builder.add("write", args -> {
               Identifier id = args.nextMap(args::getString, Identifier::parse);
               LuaTable data = args.nextTable();
               getStorage(script).set(id, LuaTableBuilder.toNbtCompound(data));
               return LuaValue.NIL;
           }, "Writes abstract NBT data to storage.", args -> {
               args.add("id", Argtypes.STRING, "Storage id to write to.");
               args.add("table", Argtypes.TABLE, "Data to write.");
           }, Argtypes.NIL);


        });
    }


    private static CommandStorage getStorage(LuaScript script) {
        return script.getSource().getServer().getCommandStorage();
    }
}
