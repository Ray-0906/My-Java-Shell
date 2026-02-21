import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        while(true){
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String comString=input.split(" ")[0];

            if(input.equals("exit")){
                break;
            } 
            if(comString.equals("echo")){
                String echoString=input.substring(5);
                System.out.println(echoString);
            } else {
            
            System.out.println(input+": command not found");
        }
    }
    }
}
