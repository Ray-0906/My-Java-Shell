package Executors.pipeline;

import Model.Command;
import ShellContext.ShellContext;

import java.io.*;


public class ExternalUnit implements ExecutionUnit  {

    private Process process;

    public ExternalUnit(Command command) throws Exception {

        ProcessBuilder pb = new ProcessBuilder(command.getArgs());
        pb.directory(ShellContext.getCurrentDir().toFile());
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        process = pb.start();
    }

    @Override
    public InputStream getStdout() {
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