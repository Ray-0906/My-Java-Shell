package Model;
import java.util.List;
public class Command {
    private final List<String> args;
    private final String stdoutRedirect;
   private  final String stderrRedirect;
    public Command(List<String> args, String stdoutRedirect, String stderrRedirect) {
        this.args = args;
        this.stdoutRedirect = stdoutRedirect;
        this.stderrRedirect = stderrRedirect;
    }

    public List<String> getArgs() { return args; }
    public String getStdoutRedirect() { return stdoutRedirect; }
    public String getStderrRedirect() { return stderrRedirect; }
}
