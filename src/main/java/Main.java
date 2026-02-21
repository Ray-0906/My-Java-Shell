import java.io.File;
import java.util.List;
import java.util.Scanner;
 
public class Main {
    public static List<String> comList = List.of("echo", "exit", "type");
     
    static void typeHandler(String typeString) {
        // String typeString=input.substring(5);
        String  res = typeString + ": not found";
        if (comList.contains(typeString)) {
             res = typeString + " is a shell builtin";
        } else {
           
            for(String path: System.getenv("PATH").split(":")){
                File file = new File(path + "/" +typeString);
                if(file.exists() && file.canExecute()){
                    res =  typeString+" is " +file.getAbsolutePath();
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
         String[] args= input.split(" ");
         String comString = args[0];   
        for(String path: System.getenv("PATH").split(":")){
            File file = new File(path + "/" +comString);
            if(file.exists() && file.canExecute()){
                args[0] = file.getAbsolutePath();
                try {
                    ProcessBuilder pb = new ProcessBuilder(args);
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
   

    

    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
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

                case "type":
                    String typeString = input.substring(5);
                    typeHandler(typeString);
                    break;
                default:
                    // System.out.println(input + ": command not found");
                    exechandler(input);
                    break;
            }

        }
    }
}
