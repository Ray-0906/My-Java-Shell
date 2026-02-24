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

            // Handle single quote
            if (isbackslash) {
                current.append(c);
                isbackslash = false;
                continue;
            }
            if (c == '\\' && !inSingleQuote) {
                isbackslash = true;
                continue;
            }
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            }
            // Handle double quote
            else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            }
            // Handle whitespace (only if outside quotes)
            else if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            }
            // Normal character
            else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}
