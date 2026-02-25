package Parser;

import java.util.List;

public class Tokenizer {
    public static List<String> tokenize(String input) {
    List<String> tokens = new java.util.ArrayList<>();
    StringBuilder current = new StringBuilder();

    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;

    for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);

        // HANDLE BACKSLASH FIRST (inside double quotes only)
        if (c == '\\' && inDoubleQuote) {
            if (i + 1 < input.length()) {
                char next = input.charAt(i + 1);

                if (next == '"' || next == '\\') {
                    current.append(next);
                    i++; // skip next char
                    continue;
                }
            }
            // literal backslash
            current.append('\\');
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
