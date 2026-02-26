package Builtins;

import Model.Command;
import IO.ShellIo;
import ShellContext.ShellContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class historyCommand {
    public static void execute(Command command, ShellIo io) {
        List<String> args = command.getArgs();

        // history -r <file> — read history from file
        if (args.size() >= 3 && args.get(1).equals("-r")) {
            String filePath = args.get(2);
            readHistoryFromFile(filePath);
            return;
        }

        // history [n] — display history
        List<String> history = ShellContext.getHistory();
        int total = history.size();

        // Default: show all history
        int count = total;

        // If argument provided, limit to last n entries
        if (args.size() > 1) {
            try {
                count = Integer.parseInt(args.get(1));
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

    private static void readHistoryFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                ShellContext.addToHistory(line);
            }
        } catch (Exception e) {
            // File not found or read error — ignore
        }
    }
}