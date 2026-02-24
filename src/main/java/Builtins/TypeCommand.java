package Builtins;

import java.io.File;

import Executors.BuiltinExecutor;
import IO.ShellIo;
import Model.Command;
public class TypeCommand {
    public static void execute(Command command,ShellIo io) {
        BuiltinExecutor builtinExecutor = BuiltinExecutor.getInstance();

        String name = command.getArgs().size() > 1
                ? command.getArgs().get(1)
                : "";

        String res = name + " not found";

        if (builtinExecutor.isBuiltin(name)) {
            res = name + " is a shell builtin";
        } else {
            for (String path : System.getenv("PATH").split(":")) {
                File file = new File(path + "/" + name);
                if (file.exists() && file.canExecute()) {
                    res = name + " is " + file.getAbsolutePath();
                    break;
                }
            }
        }
         io.println(res);
      
    }
}
