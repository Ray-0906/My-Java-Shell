package Builtins;

import ShellContext.ShellContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Model.Command;

public class CdCommand {
    static void printError(String path,Command command) {
        if(command.getStderrRedirect() != null) {
            try {
                Files.writeString(Paths.get(command.getStderrRedirect()), "cd: " + path + ": No such file or directory" + System.lineSeparator());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            System.out.println("cd: " + path + ": No such file or directory");
        }
    }
    public static void execute(Command command) {
        Path currentDir = ShellContext.getCurrentDir();
        Path homeDir = Paths.get(System.getenv("HOME"));
        List<String> parts = command.getArgs();
        String path = parts.size() > 1 ? parts.get(1) : "";

        if (path.equals("~")) {
            // Handle ~ as home directory
            currentDir = Paths.get(System.getenv("HOME"));

        } else if (path.startsWith("~/")) {

            // Handle paths starting with ~/
            Path newPath = homeDir.resolve(path.substring(2)).normalize();
            if (Files.isDirectory(newPath)) {
                currentDir = newPath;
            } else {
                printError(path, command);
            }
        } else if (path.startsWith("/")) {
            // Absolute path
            Path newPath = Paths.get(path).normalize();
            if (Files.isDirectory(newPath)) {
                currentDir = newPath;
            } else {
                printError(path, command);
            }
        }

        else {
            // Relative path
            Path newPath = currentDir.resolve(path).normalize();
            if (Files.isDirectory(newPath)) {
                currentDir = newPath;
            } else {
                printError(path, command);
            }
        }
        // Update the shell context with the new current directory if any error occurs, the current directory remains unchanged
        ShellContext.setCurrentDir(currentDir);
    }
}
