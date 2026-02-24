package Executors;

import Model.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import Builtins.CdCommand;
import Builtins.EchoCommand;
import Builtins.PwdCommand;
import Builtins.TypeCommand;
import IO.ShellIo;

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


    private ShellIo prepareIO(Command command) throws Exception {
        // Prepare the output streams based on redirection
        PrintStream out;
        PrintStream err;

        // STDOUT
        if (command.getStdoutRedirect() != null) {
            FileOutputStream fos = new FileOutputStream(
                    command.getStdoutRedirect(),
                    command.isStdoutAppend());
            out = new PrintStream(fos);
        } else {
            out = System.out;
        }

        // STDERR
        if (command.getStderrRedirect() != null) {
            FileOutputStream fos = new FileOutputStream(command.getStderrRedirect());
            err = new PrintStream(fos);
        } else {
            err = System.err;
        }

        return new ShellIo(out, err);

    }

    private void closeIo(ShellIo io) {
        // Close the streams if they are not the standard ones
        if (io.getStdout() != System.out) {
            io.getStdout().close();
        }
        if (io.getStderr() != System.err) {
            io.getStderr().close();
        }
    }

    public void execute(Command command) {
        try {

        // Prepare IO streams based on redirection
            ShellIo io = prepareIO(command);
            String name = command.getArgs().get(0);

            switch (name) {
                case "echo" -> EchoCommand.execute(command, io);
                case "cd" -> CdCommand.execute(command, io);
                case "pwd" -> PwdCommand.execute(command, io);
                case "type" -> TypeCommand.execute(command, io);
                case "exit" -> System.exit(0);
            }
            closeIo(io);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
}
