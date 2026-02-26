package Builtins;

import Model.Command;
import IO.ShellIo;

import java.util.List;

public class historyCommand {
    public static void execute(Command command, ShellIo io) {
        List<String> history = ShellContext.ShellContext.getHistory();
        int total = history.size();

        // Default: show all history
        int count = total;

        // If argument provided, limit to last n entries
        if (command.getArgs().size() > 1) {
            try {
                count = Integer.parseInt(command.getArgs().get(1));
            } catch (NumberFormatException e) {
                // ignore, show all
            }
        }

        // Calculate start index
        int start = Math.max(0, total - count);

        for (int i = start; i < total; i++) {
            io.println(String.format("%5d  %s", i + 1, history.get(i)));
        }
    }
}