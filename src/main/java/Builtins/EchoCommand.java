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

    }
}
