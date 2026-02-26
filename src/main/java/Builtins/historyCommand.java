package Builtins;

import Model.Command;
import IO.ShellIo;
import ShellContext.ShellContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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

        // history -w <file> — write all history to file
        if (args.size() >= 3 && args.get(1).equals("-w")) {
            String filePath = args.get(2);
            writeHistoryToFile(filePath);
            return;
        }

        // history -a <file> — append new commands since last flush
        if (args.size() >= 3 && args.get(1).equals("-a")) {
            String filePath = args.get(2);
            appendHistoryToFile(filePath);
            return;
        }

        // history [n] — display history
        List<String> history = ShellContext.getHistory();
        int total = history.size();

        int count = total;

        if (args.size() > 1) {
            try {
                count = Integer.parseInt(args.get(1));
            } catch (NumberFormatException e) {
                // ignore, show all
            }
        }

        int start = Math.max(0, total - count);

        for (int i = start; i < total; i++) {
            io.println(String.format("%5d  %s", i + 1, history.get(i)));
        }
    }

    private static void readHistoryFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                ShellContext.addToHistory(line);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private static void writeHistoryToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            List<String> history = ShellContext.getHistory();
            for (String entry : history) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private static void appendHistoryToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            List<String> history = ShellContext.getHistory();
            int lastFlushed = ShellContext.getLastFlushedIndex();

            // Append only commands since last flush
            for (int i = lastFlushed; i < history.size(); i++) {
                writer.write(history.get(i));
                writer.newLine();
            }

            // Update last flushed index
            ShellContext.setLastFlushedIndex(history.size());
        } catch (Exception e) {
            // ignore
        }
    }
}