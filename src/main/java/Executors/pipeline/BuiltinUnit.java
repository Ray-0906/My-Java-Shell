package Executors.pipeline;

import Executors.BuiltinExecutor;
import IO.ShellIo;
import Model.Command;

import java.io.*;

public class BuiltinUnit implements ExecutionUnit {

    private final Command command;
    private final boolean isLast;

    private final PipedInputStream stdoutRead;
    private final PipedOutputStream stdoutWrite;

    private final PipedOutputStream stdinWrite;
    private final PipedInputStream stdinRead;

    private Thread worker;

    public BuiltinUnit(Command command, boolean isLast) throws Exception {
        this.command = command;
        this.isLast = isLast;

        if (!isLast) {
            this.stdoutRead = new PipedInputStream();
            this.stdoutWrite = new PipedOutputStream(stdoutRead);
        } else {
            this.stdoutRead = null;
            this.stdoutWrite = null;
        }

        this.stdinRead = new PipedInputStream();
        this.stdinWrite = new PipedOutputStream(stdinRead);
    }

    @Override
    public void start() {
        worker = new Thread(() -> {
            try {
                PrintStream ps;
                if (isLast) {
                    ps = System.out;
                } else {
                    ps = new PrintStream(stdoutWrite, true);
                }

                ShellIo io = new ShellIo(ps, System.err);
                BuiltinExecutor.getInstance().execute(command, io);

                if (!isLast) {
                    ps.flush();
                    ps.close();
                }

                stdinRead.transferTo(OutputStream.nullOutputStream());
                stdinRead.close();
            } catch (Exception e) {
                // Broken pipe expected
            }
        });
        worker.start();
    }

    @Override
    public InputStream getStdout() {
        if (isLast) return null;
        return stdoutRead;
    }

    @Override
    public OutputStream getStdin() {
        return stdinWrite;
    }

    @Override
    public void waitFor() throws Exception {
        if (worker != null) {
            worker.join();
        }
    }
}