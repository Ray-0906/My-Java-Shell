package Builtins;

import Model.Command;
import ShellContext.ShellContext;
import java.io.FileWriter;

public class PwdCommand {
    public static void execute(Command command) {
         String redirectFile = command.getStdoutRedirect();
        if (redirectFile != null) {
            try(FileWriter fw = new FileWriter(redirectFile,command.isStdoutAppend())) {
                fw.write(ShellContext.getCurrentDir().toString() + System.lineSeparator());
                  
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Print the current working directory to the console
            System.out.println(ShellContext.getCurrentDir().toString());

        }
    }
}
