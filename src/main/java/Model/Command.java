package Model;

import java.util.List;

public class Command {

    private final List<String> args;
    private final String stdoutRedirect;
    private final boolean stdoutAppend;
    private final String stderrRedirect;
    private final boolean stderrAppend;
    public Command(List<String> args,
                   String stdoutRedirect,
                   boolean stdoutAppend,
                   String stderrRedirect,
                   boolean stderrAppend) {
        this.args = args;
        this.stdoutRedirect = stdoutRedirect;
        this.stdoutAppend = stdoutAppend;
        this.stderrRedirect = stderrRedirect;
        this.stderrAppend = stderrAppend;
    }

    public List<String> getArgs() { return args; }
    public String getStdoutRedirect() { return stdoutRedirect; }
    public boolean isStdoutAppend() { return stdoutAppend; }
    public String getStderrRedirect() { return stderrRedirect; }
    public boolean isStderrAppend() { return stderrAppend; }
}