package Parser;

import java.util.List;

import Model.Command;

public class CommandParser {

    public static Command parse(List<String> tokens) {
        if (tokens == null || tokens.isEmpty())
            return null;

        String stdout = null;
        boolean append = false;
        String stderr = null;

        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);

            if (t.equals(">") || t.equals("1>")) {
                stdout = tokens.get(i + 1);
                append = false;
                tokens.remove(i + 1);
                tokens.remove(i);
                i--;
            } else if (t.equals(">>") || t.equals("1>>")) {
                stdout = tokens.get(i + 1);
                append = true;
                tokens.remove(i + 1);
                tokens.remove(i);
                i--;
            } else if (t.equals("2>")) {
                stderr = tokens.get(i + 1);
                tokens.remove(i + 1);
                tokens.remove(i);
                i--;
            }
        }

        return new Command(tokens, stdout, append, stderr);
    }
}
