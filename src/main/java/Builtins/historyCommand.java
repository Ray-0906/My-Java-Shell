package Builtins;

import Model.Command;
import IO.ShellIo;

public class historyCommand {
    public static void execute(Command command, ShellIo io) {
        for (int i = 1; i <= ShellContext.ShellContext.getHistory().size(); i++) {
            io.println(String.format("%5d  %s", i, ShellContext.ShellContext.getHistory().get(i - 1)));
        }
    }
}