package Model;

import java.util.List;

public class Command {

    private final List<String> args;
    private final String stdoutRedirect;
    private final boolean stdoutAppend;
    private final String stderrRedirect;

    public Command(List<String> args,
                   String stdoutRedirect,
                   boolean stdoutAppend,
                   String stderrRedirect) {
        this.args = args;
        this.stdoutRedirect = stdoutRedirect;
        this.stdoutAppend = stdoutAppend;
        this.stderrRedirect = stderrRedirect;
    }

    public List<String> getArgs() { return args; }
    public String getStdoutRedirect() { return stdoutRedirect; }
    public boolean isStdoutAppend() { return stdoutAppend; }
    public String getStderrRedirect() { return stderrRedirect; }
}