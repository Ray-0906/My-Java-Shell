package Executors.pipeline;

import Model.Command;
import ShellContext.ShellContext;

import java.io.*;

public class ExternalUnit implements ExecutionUnit {

    private final Command command;
    private final boolean isLast;
    private Process process;

    public ExternalUnit(Command command, boolean isLast) {
        this.command = command;
        this.isLast = isLast;
    }

    @Override
    public void start() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command.getArgs());
        pb.directory(ShellContext.getCurrentDir().toFile());
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        if (isLast) {
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }

        process = pb.start();
    }

    @Override
    public InputStream getStdout() {
        if (isLast) return null;
        return process.getInputStream();
    }

    @Override
    public OutputStream getStdin() {
        return process.getOutputStream();
    }

    @Override
    public void waitFor() throws Exception {
        process.waitFor();
    }
}