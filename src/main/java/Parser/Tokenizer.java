package Parser;

import java.util.List;

public class Tokenizer {
    public static List<String> tokenize(String input) {
        List<String> tokens = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean isbackslash = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // HANDLE BACKSLASH FIRST (inside double quotes only)
            if (isbackslash) {
                current.append(c);
                isbackslash = false;
                continue;

            }

            if (c == '\\' && !inSingleQuote) {
                isbackslash = true;
                continue;
            }
            // SINGLE QUOTES
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            }

            // DOUBLE QUOTES
            if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            // WHITESPACE outside quotes
            if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(c);
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}
