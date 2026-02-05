package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.type.object.AbstractScriptObject;
import dev.diamond.luafy.script.type.object.ModScriptObject;
import dev.diamond.luafy.script.type.object.ScriptResultScriptObject;
import dev.diamond.luafy.script.type.object.Vec3dScriptObject;
import dev.diamond.luafy.script.type.object.game.registry.RegistryBlockScriptObject;
import dev.diamond.luafy.script.type.object.game.registry.RegistryEntityTypeScriptObject;
import dev.diamond.luafy.script.type.object.game.registry.RegistryItemScriptObject;
import dev.diamond.luafy.script.type.object.game.ItemStackScriptObject;
import dev.diamond.luafy.script.type.object.game.TextComponentScriptObject;
import dev.diamond.luafy.script.type.object.game.entity.EntityScriptObject;
import dev.diamond.luafy.script.type.object.game.entity.LivingEntityScriptObject;
import dev.diamond.luafy.script.type.object.game.entity.PlayerScriptObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ScriptObjects {

    // utility
    public static Vec3dScriptObject VEC3D = new Vec3dScriptObject();
    public static ModScriptObject MOD = new ModScriptObject();
    public static ScriptResultScriptObject SCRIPT_RESULT = new ScriptResultScriptObject();

    // entities
    public static EntityScriptObject ENTITY = new EntityScriptObject();
    public static LivingEntityScriptObject LIVING_ENTITY = new LivingEntityScriptObject();
    public static PlayerScriptObject PLAYER = new PlayerScriptObject();

    // registry objects
    public static RegistryBlockScriptObject REGISTRY_BLOCK = new RegistryBlockScriptObject();
    public static RegistryItemScriptObject REGISTRY_ITEM = new RegistryItemScriptObject();
    public static RegistryEntityTypeScriptObject REGISTRY_ENTITY_TYPE = new RegistryEntityTypeScriptObject();

    // game objects
    public static ItemStackScriptObject ITEM_STACK = new ItemStackScriptObject();
    public static TextComponentScriptObject TEXT_COMPONENT = new TextComponentScriptObject();

    public static void registerAll() {
        register(Luafy.id("vec3d"), VEC3D);
        register(Luafy.id("mod"), MOD);
        register(Luafy.id("script_result"), SCRIPT_RESULT);

        register(Luafy.id("entity"), ENTITY);
        register(Luafy.id("living_entity"), LIVING_ENTITY);
        register(Luafy.id("player"), PLAYER);

        register(Luafy.id("registry_block"), REGISTRY_BLOCK);
        register(Luafy.id("registry_item"), REGISTRY_ITEM);
        register(Luafy.id("registry_entity_type"), REGISTRY_ENTITY_TYPE);

        register(Luafy.id("item_stack"), ITEM_STACK);
        register(Luafy.id("text_component"), TEXT_COMPONENT);

        // this has to be deferred so that objects can reference each other in their docs
        AbstractScriptObject.buildAllDocs();
    }

    public static void register(Identifier id, AbstractScriptObject<?> obj) {
        Registry.register(LuafyRegistries.SCRIPT_OBJECTS, id, obj);
        Registry.register(LuafyRegistries.ARGTYPES, id, obj);
    }
}
