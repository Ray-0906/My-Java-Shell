package ShellContext;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShellContext {
    private static Path currentDir =
            Paths.get(System.getProperty("user.dir"));

    private static List<String> history = new ArrayList<>();

    private static int lastFlushedIndex = 0;

    private static String histFile = null;

    private static List<String> builtins = List.of("echo", "cd", "pwd", "type", "history", "exit");

    public static List<String> getBuiltins() {
        return builtins;
    }

    public static boolean isBuiltin(String name) {
        return builtins.contains(name);
    }

    public static void addToHistory(String command) {
        history.add(command);
    }

    public static List<String> getHistory() {
        return history;
    }

    public static int getLastFlushedIndex() {
        return lastFlushedIndex;
    }

    public static void setLastFlushedIndex(int index) {
        lastFlushedIndex = index;
    }

    public static Path getCurrentDir() {
        return currentDir;
    }

    public static void setCurrentDir(Path path) {
        currentDir = path;
    }

    public static String getHistFile() {
        return histFile;
    }

    public static void setHistFile(String path) {
        histFile = path;
    }
}