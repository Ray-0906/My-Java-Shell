
import Model.Command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.*;

import Executors.CommandExecutor;
import Parser.BuiltCompleter;
import Parser.CommandParser;
import Parser.Tokenizer;
import ShellContext.ShellContext;

public class Shell {
    private LineReader reader;

    private void loadHistoryFromFile() {
        String histFile = System.getenv("HISTFILE");
        if (histFile == null || histFile.isEmpty()) {
            return;
        }
        ShellContext.setHistFile(histFile);
        try (BufferedReader br = new BufferedReader(new FileReader(histFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                ShellContext.addToHistory(line);
            }
            // Mark all loaded entries as already flushed
            ShellContext.setLastFlushedIndex(ShellContext.getHistory().size());
        } catch (Exception e) {
            // File doesn't exist yet — ignore
        }
    }

    void run() throws Exception {
        Scanner scanner = new Scanner(System.in);
        ShellContext.setCurrentDir(Paths.get(System.getProperty("user.dir")));
         loadHistoryFromFile();
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            ShellContext.addToHistory(input);
            executeCommand(input);

        }

        // Main loop to read user input and execute commands
    }

    void runInteractive() throws Exception {

        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new BuiltCompleter())
                .parser(new DefaultParser().escapeChars(null)) // VERY IMPORTANT
                .build();

        ShellContext.setCurrentDir(Paths.get(System.getProperty("user.dir")));
         loadHistoryFromFile();
        while (true) {
            String input = reader.readLine("$ ");
            ShellContext.addToHistory(input);
            executeCommand(input);

        }
    }

    void executeCommand(String input) throws Exception {

        List<String> tokenizer = Tokenizer.tokenize(input);

        Command command = CommandParser.parse(tokenizer);
        // System.err.println("Parsed command: " + (command.isPipeline() ? " (pipeline)"
        // : "not a pipeline") );
        if (command.isPipeline()) {

            CommandExecutor.getInstance().execute(command);
            return;
        }

        if (command.getArgs() == null || command.getArgs().isEmpty()) {
            return;
        }

        CommandExecutor.getInstance().execute(command);
        // Parse the input into a Command object and execute it
    }

}
