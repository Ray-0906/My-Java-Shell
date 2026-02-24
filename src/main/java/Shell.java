
import Model.Command;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import Executors.CommandExecutor;
import Parser.CommandParser;
import Parser.Tokenizer;
import ShellContext.ShellContext;
import java.nio.file.Paths;

public class Shell {

    void run() {
        Scanner scanner = new Scanner(System.in);
        ShellContext.setCurrentDir(Paths.get(System.getProperty("user.dir")));
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            executeCommand(input);
        }
        // Main loop to read user input and execute commands
    }

    void executeCommand(String input) {

        List<String> tokenizer = Tokenizer.tokenize(input);
        Command command = CommandParser.parse(tokenizer);
        if (command == null || command.getArgs().isEmpty()) {
            return;
        }
        CommandExecutor.getInstance().execute(command);
        // Parse the input into a Command object and execute it
    }

}
