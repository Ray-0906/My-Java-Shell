package Builtins;

import Model.Command;
import ShellContext.ShellContext;


import IO.ShellIo;

public class PwdCommand {
    public static void execute(Command command,ShellIo io) {
        
         io.println(ShellContext.getCurrentDir().toString());   
      
    }
}
