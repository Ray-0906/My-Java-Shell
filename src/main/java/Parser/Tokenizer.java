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

            // ===== SINGLE QUOTES =====
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                } else {
                    current.append(c);
                }
                continue;
            }

            // ===== DOUBLE QUOTES =====
            if (inDoubleQuote) {

                if (c == '\\') {
                    if (i + 1 < input.length()) {
                        char next = input.charAt(i + 1);

                        if (next == '"' || next == '\\' || next == '$' || next == '`') {
                            current.append(next);
                            i++;
                        } else {
                            // literal backslash
                            current.append('\\');
                        }
                    } else {
                        current.append('\\');
                    }
                    continue;
                }

                if (c == '"') {
                    inDoubleQuote = false;
                    continue;
                }

                current.append(c);
                continue;
            }

            // ===== NORMAL (outside quotes) =====

            if (c == '\\') {
                if (i + 1 < input.length()) {
                    current.append(input.charAt(i + 1));
                    i++;
                }
                continue;
            }

            if (c == '\'') {
                inSingleQuote = true;
                continue;
            }

            if (c == '"') {
                inDoubleQuote = true;
                continue;
            }

            if (Character.isWhitespace(c)) {
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
