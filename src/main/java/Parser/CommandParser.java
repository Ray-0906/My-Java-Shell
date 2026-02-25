package Parser;

import java.util.ArrayList;
import java.util.List;

import Model.Command;

public class CommandParser {
    public static Command parse(List<String> tokens) {
        if (tokens.contains("|")) {
            return parsePipeline(tokens);
        }
        return parseSingleCommand(tokens);
    }

    private static Command parsePipeline(List<String> tokens) {

        List<Command> pipelineCommands = new ArrayList<>();
        List<String> current = new ArrayList<>();

        for (String token : tokens) {

            if (token.equals("|")) {
                Command command = parseSingleCommand(current);
                if (command != null) {

                    pipelineCommands.add(command);

                }
                current.clear();

            } else {
                current.add(token);
            }
        }

        // Add last segment
        if (!current.isEmpty()) {
            Command command = parseSingleCommand(current);
            if (command != null) {

                pipelineCommands.add(command);

            }
            
        }

        Command pipeline = new Command();
        pipeline.setPipelineCommands(pipelineCommands);

        return pipeline;
    }

    public static Command parseSingleCommand(List<String> tokens) {
        if (tokens == null || tokens.isEmpty())
            return null;

        String stdout = null;
        boolean append = false;
        String stderr = null;
        boolean stderrAppend = false;

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
            } else if (t.equals("2>>")) {
                stderr = tokens.get(i + 1);
                stderrAppend = true;
                tokens.remove(i + 1);
                tokens.remove(i);
                i--;
            }
        }

        return new Command(tokens, stdout, append, stderr, stderrAppend);
    }

}
