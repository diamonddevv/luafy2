package dev.diamond.luafy.autodoc;

import dev.diamond.luafy.script.type.Argtype;

import java.util.ArrayList;

public class ArglistBuilder {
    public final ArrayList<ArgDocInfo> args;

    public ArglistBuilder() {
        this.args = new ArrayList<>();
    }

    public ArglistBuilder add(String argName, Argtype argType, String argDesc) {
        this.args.add(new ArgDocInfo(argName, argType, argDesc));
        return this; // return this so you can method chain if you want
    }

    public ArrayList<ArgDocInfo> build() {
        return args;
    }
}
