package Executors.pipeline;

import Executors.BuiltinExecutor;
import IO.ShellIo;
import Model.Command;

import java.io.*;

public class BuiltinUnit implements ExecutionUnit {

    private final Command command;
    private final boolean isLast;
    private final boolean isFirst;

    private final PipedInputStream stdoutRead;
    private final PipedOutputStream stdoutWrite;

    private PipedOutputStream stdinWrite;
    private PipedInputStream stdinRead;

    private Thread worker;

    public BuiltinUnit(Command command, boolean isFirst, boolean isLast) throws Exception {
        this.command = command;
        this.isFirst = isFirst;
        this.isLast = isLast;

        if (!isLast) {
            this.stdoutRead = new PipedInputStream();
            this.stdoutWrite = new PipedOutputStream(stdoutRead);
        } else {
            this.stdoutRead = null;
            this.stdoutWrite = null;
        }

        // Only create stdin pipe if not the first command
        if (!isFirst) {
            this.stdinRead = new PipedInputStream();
            this.stdinWrite = new PipedOutputStream(stdinRead);
        } else {
            this.stdinRead = null;
            this.stdinWrite = null;
        }
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

                // Drain remaining stdin so upstream doesn't block
                if (stdinRead != null) {
                    stdinRead.transferTo(OutputStream.nullOutputStream());
                    stdinRead.close();
                }
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