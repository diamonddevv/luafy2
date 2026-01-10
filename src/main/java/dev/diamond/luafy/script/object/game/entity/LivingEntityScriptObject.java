package dev.diamond.luafy.script.object.game.entity;

import dev.diamond.luafy.autodoc.Argtypes;
import dev.diamond.luafy.lua.LuaTableBuilder;
import dev.diamond.luafy.registry.ScriptObjects;
import dev.diamond.luafy.script.LuaScript;
import dev.diamond.luafy.script.object.AbstractScriptObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Optional;


public class LivingEntityScriptObject extends AbstractScriptObject<LivingEntity> {

    public static final String FUNC_GET_HEALTH = "get_health";

    public LivingEntityScriptObject() {
        super("A living entity.", doc -> {
            doc.addFunction(FUNC_GET_HEALTH, "Returns this entities health.", args -> {

            }, Argtypes.NUMBER);
        });
    }

    @Override
    public void toTable(LivingEntity obj, LuaTableBuilder builder, LuaScript script) {
        applyInheritanceToTable(obj, builder, script);

        builder.add(FUNC_GET_HEALTH, args -> LuaValue.valueOf(obj.getHealth()));

        makeReadonly(builder);
    }

    @Override
    public LivingEntity toThing(LuaTable table, ServerCommandSource src, LuaScript script) {
        return (LivingEntity) ScriptObjects.ENTITY.toThing(table, src, script);
    }

    @Override
    public Optional<AbstractScriptObject<? super LivingEntity>> getParentType() {
        return Optional.of(ScriptObjects.ENTITY);
    }

    @Override
    public String getArgtypeString() {
        return "LivingEntity";
    }
}
