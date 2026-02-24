package Executors;

import Model.Command;
import ShellContext.ShellContext;
import java.io.File;

public class ExternalExecutor {
    private static ExternalExecutor instance = null;
    private ExternalExecutor() {}
    public static ExternalExecutor getInstance() {
        if(instance == null) {
            instance = new ExternalExecutor();
        }
        return instance;
    }

    public void execute(Command command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command.getArgs());
            pb.directory(ShellContext.getCurrentDir().toFile());

            if (command.getStdoutRedirect() != null) {
                pb.redirectOutput(new File(command.getStdoutRedirect()));
            } else {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }

            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            System.out.println(command.getArgs().get(0) + ": command not found");
        }
    }
}
