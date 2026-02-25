package Executors.pipeline;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.channels.Pipe;

import Executors.BuiltinExecutor;
import IO.ShellIo;
import Model.Command;

public class BuiltinUnit implements ExecutionUnit {
    private PipedInputStream stdout;
    private PipedOutputStream stdoutWriter;

    public BuiltinUnit(Command command) throws Exception {

        this.stdout = new PipedInputStream();
        this.stdoutWriter = new PipedOutputStream(this.stdout);

        // Execute the builtin command and write its output to stdoutWriter
        new Thread(() -> {
            try (PrintStream ps = new PrintStream(stdoutWriter)) {

                ShellIo io = new ShellIo(ps, System.err);
                BuiltinExecutor.getInstance().execute(command, io);
                ps.flush();
                stdoutWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public InputStream getStdout() {
        return stdout;
    }

    @Override
    public OutputStream getStdin() {
        return null; // for now builtins don’t consume pipeline input
    }

    @Override
    public void waitFor() {
        // nothing required
    }

}
