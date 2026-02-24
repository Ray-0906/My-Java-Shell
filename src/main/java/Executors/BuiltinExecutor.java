package Executors;

import Model.Command;

import java.io.File;
import java.util.List;
import Builtins.CdCommand;
import Builtins.EchoCommand;
import Builtins.PwdCommand;
import Builtins.TypeCommand;

public class BuiltinExecutor {
    // Singleton pattern to ensure only one instance of BuiltinExecutor exists
    private static BuiltinExecutor instance;

    private BuiltinExecutor() {
    }

    public static BuiltinExecutor getInstance() {
        if (instance == null) {
            instance = new BuiltinExecutor();
        }
        return instance;
    }

    public boolean isBuiltin(String name) {
        return List.of("echo", "cd", "pwd", "type", "exit").contains(name);
    }

    private void ensureRedirectFiles(Command command) {
        try {
            if (command.getStdoutRedirect() != null) {
                new File(command.getStdoutRedirect()).createNewFile();
            }
            if (command.getStderrRedirect() != null) {
                new File(command.getStderrRedirect()).createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(Command command) {
        String name = command.getArgs().get(0);
        // Ensure that redirect files exist before executing the command
        ensureRedirectFiles(command);
        switch (name) {
            case "echo" -> EchoCommand.execute(command);
            case "cd" -> CdCommand.execute(command);
            case "pwd" -> PwdCommand.execute(command);
            case "type" -> TypeCommand.execute(command);
            case "exit" -> System.exit(0);
        }
    }
}
