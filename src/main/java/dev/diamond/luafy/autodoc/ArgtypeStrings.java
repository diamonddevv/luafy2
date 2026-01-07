package dev.diamond.luafy.autodoc;

public class ArgtypeStrings {
    public static final String NIL = "nil";
    public static final String BOOLEAN = "boolean";
    public static final String NUMBER = "number";
    public static final String STRING = "string";
    public static final String VALUE = "any";
    public static final String INTEGER = "integer";
    public static final String TABLE = "table";

    public static String array(String argtype) {
        return argtype + "[]";
    }
}
