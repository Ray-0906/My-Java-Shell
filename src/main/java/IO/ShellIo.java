package IO;

import java.io.PrintStream;

public class ShellIo {
    // This class encapsulates the input and output streams for the shell, allowing built-in commands to write to the correct destinations (stdout, stderr) based on redirection.
    private final PrintStream stdout;
    private final PrintStream stderr;
    
// Constructor to initialize the ShellIo with the appropriate output streams
    public ShellIo(PrintStream stdout, PrintStream stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }
// Method to print a line to stdout
    public void println(String message) {
        stdout.println(message);
    }
// You can add more methods for printing to stdout or stderr as needed
    public void print(String message) {
        stdout.print(message);
    }
// You can add more methods for reading input if needed, but for built-in commands, we mostly care about output.
    public void error(String message) {
        stderr.println(message);
    }
// Getters for the streams, in case built-in commands need to write directly to them
    public PrintStream getStdout() {
        return stdout;
    }
// Getters for the streams, in case built-in commands need to write directly to them
    public PrintStream getStderr() {
        return stderr;
    }
}
