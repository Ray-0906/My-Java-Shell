package Executors.pipeline;

import Executors.BuiltinExecutor;
import IO.ShellIo;
import Model.Command;

import java.io.*;

public class BuiltinUnit implements ExecutionUnit {

    private final Command command;

    private final PipedInputStream stdout;
    private final PipedOutputStream stdoutWriter;

    private Thread worker;

    public BuiltinUnit(Command command) throws Exception {
        this.command = command;

        this.stdout = new PipedInputStream();
        this.stdoutWriter = new PipedOutputStream(stdout);
    }

    @Override
    public void start() {

        worker = new Thread(() -> {
            try (PrintStream ps = new PrintStream(stdoutWriter)) {

                ShellIo io = new ShellIo(ps, System.err);
                BuiltinExecutor.getInstance().execute(command, io);

                ps.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        worker.start();
    }

    @Override
    public InputStream getStdout() {
        return stdout;
    }

    @Override
    public OutputStream getStdin() {
        return null; // still fine for now
    }

    @Override
    public void waitFor() throws InterruptedException {
        if (worker != null) {
            worker.join();
        }
    }
}