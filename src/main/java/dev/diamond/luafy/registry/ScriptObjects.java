package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.object.*;
import dev.diamond.luafy.script.object.game.BlockScriptObject;
import dev.diamond.luafy.script.object.game.ItemScriptObject;
import dev.diamond.luafy.script.object.game.ItemStackScriptObject;
import dev.diamond.luafy.script.object.game.entity.EntityScriptObject;
import dev.diamond.luafy.script.object.game.entity.LivingEntityScriptObject;
import dev.diamond.luafy.script.object.game.entity.PlayerScriptObject;
import net.minecraft.core.Registry;

public class ScriptObjects {

    // utility
    public static Vec3dScriptObject VEC3D = new Vec3dScriptObject();
    public static ModScriptObject MOD = new ModScriptObject();
    public static ScriptResultScriptObject SCRIPT_RESULT = new ScriptResultScriptObject();
    public static NbtTableScriptObject NBT_TABLE = new NbtTableScriptObject();

    // entities
    public static EntityScriptObject ENTITY = new EntityScriptObject();
    public static LivingEntityScriptObject LIVING_ENTITY = new LivingEntityScriptObject();
    public static PlayerScriptObject PLAYER = new PlayerScriptObject();

    // game objects
    public static BlockScriptObject BLOCK = new BlockScriptObject();
    public static ItemScriptObject ITEM = new ItemScriptObject();
    public static ItemStackScriptObject ITEM_STACK = new ItemStackScriptObject();

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("vec3d"), VEC3D);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("mod"), MOD);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("script_result"), SCRIPT_RESULT);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("nbt_table"), NBT_TABLE);

        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("entity"), ENTITY);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("living_entity"), LIVING_ENTITY);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("player"), PLAYER);

        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("block"), BLOCK);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("item"), ITEM);
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, Luafy.id("item_stack"), ITEM_STACK);

        // this has to be deferred so that objects can reference each other in their docs
        AbstractScriptObject.buildAllDocs();
    }
}
