package Parser;

import java.util.List;

import Model.Command;

public class CommandParser {
    
    public static Command parse(List<String> tokens) {
        String stdoutRedirect = null;
        String stderrRedirect = null;

        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals(">") || tokens.get(i).equals("1>")) {
                stdoutRedirect = tokens.get(i + 1);
                tokens.remove(i + 1);
                tokens.remove(i);
                break;
            }
            else if(tokens.get(i).equals("2>")) {
                stderrRedirect = tokens.get(i + 1);
                tokens.remove(i + 1);
                tokens.remove(i);
                break;
            }
        }

        return new Command(tokens, stdoutRedirect, stderrRedirect);
    }
}
