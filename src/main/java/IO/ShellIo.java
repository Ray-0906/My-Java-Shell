package IO;

import java.io.PrintStream;

public class ShellIo {
    private final PrintStream stdout;
    private final PrintStream stderr;

    public ShellIo(PrintStream stdout, PrintStream stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public void println(String message) {
        stdout.println(message);
    }

    public void print(String message) {
        stdout.print(message);
    }

    public void error(String message) {
        stderr.println(message);
    }

    public PrintStream getStdout() {
        return stdout;
    }

    public PrintStream getStderr() {
        return stderr;
    }
}
