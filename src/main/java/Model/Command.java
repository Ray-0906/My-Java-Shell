package Model;

import java.util.List;

public class Command {
    private List<Command> pipelineCommands;
    private final List<String> args;
    private final String stdoutRedirect;
    private final boolean stdoutAppend;
    private final String stderrRedirect;
    private final boolean stderrAppend;
    public Command() {
        this.args = null;
        this.stdoutRedirect = null;
        this.stdoutAppend = false;
        this.stderrRedirect = null;
        this.stderrAppend = false;
    }
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
    public List<Command> getPipelineCommands() { return pipelineCommands; }
    public void setPipelineCommands(List<Command> pipelineCommands) {
        this.pipelineCommands = pipelineCommands;
    }
    public boolean isPipeline() {
        return pipelineCommands != null && !pipelineCommands.isEmpty();
    }
}