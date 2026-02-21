import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        while(true){
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String comString=input.split(" ")[0];
            List<String> comList=List.of("echo","exit","type");  

            switch (comString) {
                case "echo":
                    String echoString=input.substring(5);
                    System.out.println(echoString);
                    break;
                case "exit":
                    System.exit(0);

                case "type":
                    String typeString=input.substring(5);
                    if(!comList.contains(typeString)){
                        typeString=typeString+": command not found";
                    }
                    else{
                        typeString=typeString+" is a shell builtin";
                    }
                    System.out.println(typeString);
                    break;    
                default:
                    System.out.println(input+": command not found");
                    break;
            }
            
    }
    }
}
