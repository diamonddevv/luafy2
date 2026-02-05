package dev.diamond.luafy.autodoc;

import dev.diamond.luafy.script.type.Argtype;

import java.util.ArrayList;

public record FunctionDocInfo(String funcName, String funcDesc, ArrayList<ArgDocInfo> args,
                              Argtype returnType) implements SimpleAutodocumentable {

    @Override
    public String generateAutodocString() {
        StringBuilder s = new StringBuilder();

        s.append(funcName());
        s.append("(");
        for (int i = 0; i < args().size(); i++) {
            var arg = args().get(i);
            s.append(arg.argName());
            s.append(": ");
            s.append(arg.argType());
            if (i < args().size() - 1) {
                s.append(", ");
            }
        }
        s.append("): ");
        s.append(returnType().getArgtypeString());
        s.append("\n");
        s.append(funcDesc());
        s.append("\nParameters:\n");
        if (!args().isEmpty()) {
            for (var arg : args()) {
                s.append("    - ");
                s.append(arg.argName());
                s.append(" : ");
                s.append(arg.argDesc());
                s.append("\n");
            }
        } else {
            s.append("    None\n");
        }
        s.append("\n");

        return s.toString();
    }
}
