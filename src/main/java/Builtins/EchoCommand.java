package Builtins;

import Model.Command;
import java.io.FileWriter;

import IO.ShellIo;
public class EchoCommand {

    public static void execute(Command command, ShellIo io) {
        // Join the arguments (excluding the command name) into a single string
        String output = String.join(" ",
            command.getArgs().subList(1, command.getArgs().size()));

            io.println(output);

        // if (command.getStdoutRedirect() != null) {
        //     // Write the output to the specified file instead of printing to console
        //     try (FileWriter fw =
        //              new FileWriter(command.getStdoutRedirect(),command.isStdoutAppend())) {
                      
        //                      fw.write(output + System.lineSeparator());
                        
                   
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // } else {
        //     // Print the output to the console
        //     io.(output);
        // }
    }
}
