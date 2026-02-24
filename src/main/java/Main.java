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
        List<String> tokens = parseInput(input);
        if (tokens.isEmpty())
            return;

        String outFileString=null;

        for(int i=0;i<tokens.size();i++){
            if(tokens.get(i).equals(">") ||  tokens.get(i).equals("1>")){
                if(i+1>=tokens.size()){
                    System.out.println("Syntax error: expected file after " + tokens.get(i));
                    return;
                }
                outFileString=tokens.get(i+1);
                tokens.remove(i+1);
                tokens.remove(i);
                break;
            }
        }

        String[] args = tokens.toArray(new String[0]);

        try {
            ProcessBuilder pb = new ProcessBuilder(args);
             pb.directory(currentDir.toFile());
            if(outFileString!=null){
                pb.redirectOutput(new File(outFileString));
                
            }
            else{
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
               
            }
           pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println(tokens.get(0) + ": command not found");
        }
    }

    static void cdHandler(String input) {
        String[] parts = input.split(" ", 2);
        String path = parts.length > 1 ? parts[1] : "";
        if (path.equals("~")) {
            currentDir = Paths.get(System.getenv("HOME"));

        } else if (path.startsWith("~/")) {
            Path newPath = homeDir.resolve(path.substring(2)).normalize();
            if (Files.isDirectory(newPath)) {
                currentDir = newPath;
            } else {
                System.out.println("cd: " + path + ": No such file or directory");
            }
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

    static List<String> parseInput(String input) {
        List<String> tokens = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean isbackslash = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Handle single quote
            if (isbackslash) {
                current.append(c);
                isbackslash = false;
                continue;
            }
            if (c == '\\' && !inSingleQuote) {
                isbackslash = true;
                continue;
            }
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            }
            // Handle double quote
            else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            }
            // Handle whitespace (only if outside quotes)
            else if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            }
            // Normal character
            else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage

        currentDir = Paths.get(System.getProperty("user.dir"));
        homeDir = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            List<String> tokens = parseInput(input);

            if (tokens.isEmpty())
                continue;
    
            String comString = tokens.get(0);

            switch (comString) {
                case "echo":
                    // String echoString = input.substring(5);
                    String parsed = parseInput(input).stream().skip(1).reduce((a, b) -> a + " " + b).orElse("");
                    System.out.println(parsed);
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
