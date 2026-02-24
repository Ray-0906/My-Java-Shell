package Executors;

import Model.Command;
import ShellContext.ShellContext;
import java.io.File;

public class ExternalExecutor {
    private static ExternalExecutor instance = null;

    private ExternalExecutor() {
    }

    public static ExternalExecutor getInstance() {
        if (instance == null) {
            instance = new ExternalExecutor();
        }
        return instance;
    }

    public void execute(Command command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command.getArgs());
            pb.directory(ShellContext.getCurrentDir().toFile());
            // Handle stdout and stderr redirection
            if (command.getStdoutRedirect() != null) {
                File file = new File(command.getStdoutRedirect());

                if (command.isStdoutAppend()) {
                    pb.redirectOutput(ProcessBuilder.Redirect.appendTo(file));
                } else {
                    pb.redirectOutput(file); // overwrite
                }
            } else {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            // Handle stderr redirection
            if (command.getStderrRedirect() != null) {
                File file = new File(command.getStderrRedirect());
                if (command.isStderrAppend()) {
                    pb.redirectError(ProcessBuilder.Redirect.appendTo(file));
                } else {
                    pb.redirectError(file);
                }

            } else {
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }
            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            System.out.println(command.getArgs().get(0) + ": command not found");
        }
    }
}
