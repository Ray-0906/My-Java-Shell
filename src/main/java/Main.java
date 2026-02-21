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
           
            for(String path: System.getenv().get("PATH").split(":")){
                if(isExec(path, typeString)){
                    res =  typeString+" is " +path + "/" + typeString;
                    break;
                }
            }
           
        }
        System.out.println(res);
    }
    static boolean isExec(String pat, String comString) {
        File file = new File(pat + "/" + comString);
        return file.exists() && file.canExecute();
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
                    System.out.println(input + ": command not found");
                    break;
            }

        }
    }
}
