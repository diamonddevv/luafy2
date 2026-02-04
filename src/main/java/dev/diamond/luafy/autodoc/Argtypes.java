package dev.diamond.luafy.autodoc;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Either;
import org.luaj.vm2.*;

import java.util.List;
import java.util.Optional;

public class Argtypes {

    // since these are "primitives" they are in lowercase; this also ensures parity with LuaCATS and lua
    // other argtypes (enums, classes) should adhere to PascalCase like java class names

    public static final Argtype<?, ?> NIL = Argtype.of("nil");
    public static final Argtype<LuaBoolean, Boolean> BOOLEAN = Argtype.of("boolean", BoolArgumentType.bool(), (ctx, s) -> LuaValue.valueOf(BoolArgumentType.getBool(ctx, s)), null);
    public static final Argtype<LuaNumber, Float> NUMBER = Argtype.of("number", FloatArgumentType.floatArg(), (ctx, s) -> LuaValue.valueOf(FloatArgumentType.getFloat(ctx, s)), null);
    public static final Argtype<LuaString, String> STRING = Argtype.of("string", StringArgumentType.string(), (ctx, s) -> LuaValue.valueOf(StringArgumentType.getString(ctx, s)), null);
    public static final Argtype<LuaValue, ?> VALUE = Argtype.of("any");
    public static final Argtype<LuaInteger, Integer> INTEGER = Argtype.of("integer", IntegerArgumentType.integer(), (ctx, s) -> LuaValue.valueOf(IntegerArgumentType.getInteger(ctx, s)), null);
    public static final Argtype<LuaTable, ?> TABLE = Argtype.of("table");


    public static Argtype<?, ?> array(Argtype<?, ?> argtype) {
        return Argtype.of(argtype.getArgtypeString() + "[]");
    }
    public static Argtype<?, ?> or(Argtype<?, ?> a, Argtype<?, ?> b) {
        return Argtype.of(a.getArgtypeString() + " | " + b.getArgtypeString());
    }
    public static Argtype<?, ?> maybe(Argtype<?, ?> argtype) {
        return Argtype.of(argtype.getArgtypeString() + " | nil");
    }
}
