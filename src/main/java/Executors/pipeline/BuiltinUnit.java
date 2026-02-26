package Executors.pipeline;

import Executors.BuiltinExecutor;
import IO.ShellIo;
import Model.Command;

import java.io.*;

public class BuiltinUnit implements ExecutionUnit {

    private final Command command;

    private final PipedInputStream stdoutRead;
    private final PipedOutputStream stdoutWrite;

    private final PipedOutputStream stdinWrite;
    private final PipedInputStream stdinRead;

    private Thread worker;

    public BuiltinUnit(Command command) throws Exception {
        this.command = command;

        // Stdout pipe: builtin writes to stdoutWrite, downstream reads from stdoutRead
        this.stdoutRead = new PipedInputStream();
        this.stdoutWrite = new PipedOutputStream(stdoutRead);

        // Stdin pipe: upstream writes to stdinWrite, builtin reads from stdinRead
        this.stdinRead = new PipedInputStream();
        this.stdinWrite = new PipedOutputStream(stdinRead);
    }

    @Override
    public void start() {
        worker = new Thread(() -> {
            try {
                PrintStream ps = new PrintStream(stdoutWrite, true);
                ShellIo io = new ShellIo(ps, System.err);

                BuiltinExecutor.getInstance().execute(command, io);

                ps.flush();
                ps.close();

                // Drain any remaining stdin so upstream doesn't block
                stdinRead.transferTo(OutputStream.nullOutputStream());
                stdinRead.close();

            } catch (Exception e) {
                // Broken pipe is expected if downstream closes early
            }
        });
        worker.start();
    }

    @Override
    public InputStream getStdout() {
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