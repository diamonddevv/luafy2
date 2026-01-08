package dev.diamond.luafy.autodoc;

public class Argtypes {

    // since these are "primitives" they are in lowercase; this also ensures parity with LuaCATS and lua
    // other argtypes (enums, classes) should adhere to PascalCase like java class names

    public static final Argtype NIL = () -> "nil";
    public static final Argtype BOOLEAN = () -> "boolean";
    public static final Argtype NUMBER = () -> "number";
    public static final Argtype STRING = () -> "string";
    public static final Argtype VALUE = () -> "any";
    public static final Argtype INTEGER = () -> "integer";
    public static final Argtype TABLE = () -> "table";


    public static Argtype array(Argtype argtype) {
        return () -> argtype.getArgtypeString() + "[]";
    }
    public static Argtype maybe(Argtype argtype) {
        return () -> argtype.getArgtypeString() + "?";
    }
}
