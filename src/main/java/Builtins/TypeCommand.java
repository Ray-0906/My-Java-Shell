package Builtins;

import java.io.File;

import Executors.BuiltinExecutor;
import Model.Command;
public class TypeCommand {
    public static void execute(Command command) {
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

        try {
            if (command.getStdoutRedirect() != null) {
                try (java.io.FileWriter fw = new java.io.FileWriter(command.getStdoutRedirect())) {
                    fw.write(res + System.lineSeparator());
                }
            } else {
                System.out.println(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
