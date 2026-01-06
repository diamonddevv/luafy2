package dev.diamond.luafy.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class MetamethodImpl {

    /**
     * LuaJ doesn't seem to implement the metatag <code>__tostring</code> very well.<br><br>
     *
     * If <code>value</code> is a <code>LuaTable</code>, and <code>__tostring</code> is present, <code>__tostring</code>
     * will be called to convert it to a string before returning it. Any other value uses the default LuaJ behaviour of
     * <code>LuaValue#tojstring</code>.
     *
     * @see MetamethodNames
     * @see org.luaj.vm2.LuaValue
     * @return <code>value</code> as a string.
     */
    public static String tostring(LuaValue value) {
        if (value.istable()) {
            LuaTable table = value.checktable();
            if (!table.metatag(MetamethodNames.TO_STRING).isnil()) {
                value = table.metatag(MetamethodNames.TO_STRING).call();
            }
        }

        return value.tojstring();
    }

}
