package ShellContext;
import java.nio.file.Path;
import java.nio.file.Paths;
public class ShellContext {
    private static Path currentDir =
            Paths.get(System.getProperty("user.dir"));

    public static Path getCurrentDir() { return currentDir; }
    public static void setCurrentDir(Path path) { currentDir = path; }
}
