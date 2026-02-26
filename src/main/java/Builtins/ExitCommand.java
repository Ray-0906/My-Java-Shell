package Builtins;
import IO.ShellIo;
import Model.Command;
import ShellContext.ShellContext;

public class ExitCommand {
    private static void saveHistoryToFile() {
        String histFile = ShellContext.getHistFile();
        if (histFile == null || histFile.isEmpty()) {
            return;
        }
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(histFile))) {
            java.util.List<String> history = ShellContext.getHistory();
            for (String entry : history) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (Exception e) {
            // ignore
        }
    }
    public static void execute(Command  command, ShellIo  io) {
        saveHistoryToFile();
         System.exit(Integer.parseInt(
                    command.getArgs().size() > 1 ? command.getArgs().get(1) : "0"));
    }
}
