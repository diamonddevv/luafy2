package dev.diamond.luafy.script.enumeration;

import dev.diamond.luafy.autodoc.Argtype;
import dev.diamond.luafy.autodoc.SimpleAutodocumentable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScriptEnum<E extends Enum<E>> implements SimpleAutodocumentable, Argtype {

    private final Class<E> enumClass;

    public ScriptEnum(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    private String getEnumName() {
        return enumClass.getSimpleName();
    }

    public Collection<String> getEnumKeys() {
        return Arrays.stream(enumClass.getEnumConstants()).map(E::name).collect(Collectors.toSet());
    }

    public String toKey(E e) {
        return e.name();
    }

    public E fromKey(String key) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(e -> Objects.equals(key, e.name())).findFirst().orElseThrow();
    }

    @Override
    public String generateAutodocString() {
        StringBuilder b = new StringBuilder();

        return b.toString();
    }

    @Override
    public String getArgtypeString() {
        return getEnumName();
    }
}
