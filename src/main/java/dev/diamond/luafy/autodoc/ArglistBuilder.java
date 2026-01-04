package dev.diamond.luafy.autodoc;

import java.util.ArrayList;

public class ArglistBuilder {
    public final ArrayList<ArgDocInfo> args;

    public ArglistBuilder() {
        this.args = new ArrayList<>();
    }

    public void add(String argName, String argType, String argDesc) {
        this.args.add(new ArgDocInfo(argName, argType, argDesc));
    }
}
