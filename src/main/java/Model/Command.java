package Model;
import java.util.List;
public class Command {
    private final List<String> args;
    private final String stdoutRedirect;

    public Command(List<String> args, String stdoutRedirect) {
        this.args = args;
        this.stdoutRedirect = stdoutRedirect;
    }

    public List<String> getArgs() { return args; }
    public String getStdoutRedirect() { return stdoutRedirect; }
}
