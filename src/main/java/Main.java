import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Path currentDir;
    static Path homeDir;

    public static List<String> comList = List.of("echo", "exit", "type", "pwd", "cd");

    static void typeHandler(String typeString) {
        // String typeString=input.substring(5);
        String res = typeString + ": not found";
        if (comList.contains(typeString)) {
            res = typeString + " is a shell builtin";
        } else {

            for (String path : System.getenv("PATH").split(":")) {
                File file = new File(path + "/" + typeString);
                if (file.exists() && file.canExecute()) {
                    res = typeString + " is " + file.getAbsolutePath();
                    break;
                }
            }

        }
        System.out.println(res);
    }

    boolean isExecutable(String path) {
        File file = new File(path);
        return file.exists() && file.canExecute();
    }

    static void exechandler(String input) {
        String[] args = input.split(" ");
        String comString = args[0];
        for (String path : System.getenv("PATH").split(":")) {
            File file = new File(path + "/" + comString);
            if (file.exists() && file.canExecute()) {
                // args[0] = file.getAbsolutePath();
                try {
                    ProcessBuilder pb = new ProcessBuilder(args);
                    pb.directory(new File(path));
                    pb.inheritIO();
                    Process process = pb.start();
                    process.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        System.out.println(input + ": command not found");

    }

    static void cdHandler(String input) {
        String path = input.substring(3);
        if (path.equals("~")) {
            currentDir =  Paths.get(System.getenv("HOME"));
            
        } else if (path.startsWith("/")) {
            Path newPath = Paths.get(path).normalize();
            if (Files.isDirectory(newPath)) {

                currentDir = newPath;
            } else {
                System.out.println("cd: " + path + ": No such file or directory");
            }
        }

        else {
            Path newPath = currentDir.resolve(path).normalize();
            if (Files.isDirectory(newPath)) {
                currentDir = newPath;
            } else {
                System.out.println("cd: " + path + ": No such file or directory");
            }
        }

    }

    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage

        currentDir = Paths.get(System.getProperty("user.dir"));
        ;
        homeDir = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String comString = input.split(" ")[0];

            switch (comString) {
                case "echo":
                    String echoString = input.substring(5);
                    System.out.println(echoString);
                    break;
                case "exit":
                    System.exit(0);
                case "pwd":
                    System.out.println(currentDir.toAbsolutePath().toString());
                    break;

                case "type":
                    String typeString = input.substring(5);
                    typeHandler(typeString);
                    break;
                case "cd":
                    cdHandler(input);
                    break;
                default:
                    // System.out.println(input + ": command not found");
                    exechandler(input);
                    break;
            }

        }
    }
}
