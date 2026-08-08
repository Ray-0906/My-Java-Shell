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
                  //  the outputstream of the stdout pipe is wrapped in Printstream  and then used as shellio for builtin processes
                  // whenever built in processes write anything to the outputstream of the stdout it appears in the stdout pipe other end which is stdinread(pipedInput stream)
                  //stdinread(pipedInput stream)  transfer bytes  to downstream process whenever  later when conntect via (srt.transferTo(dst))
                ShellIo io = new ShellIo(ps, System.err);
                BuiltinExecutor.getInstance().execute(command, io);

                if (!isLast) {
                    ps.flush();
                    ps.close();
                }

                if (stdinRead != null) {
                    // stdinread is the other in of the input pipe the stdinWrite is the end which connects to upstream thread as dst
                    // so as the pipe maybe full and upstrream may get blocked so .. stdinread (other end of ip pipe) transfer the stream to null output stream 
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

    @Override
    public void destroy() {
        if (worker != null) {
            worker.interrupt();
        }
    }
}