package Builtins;

import java.io.File;

import Executors.BuiltinExecutor;

public class TypeCommand {

    public static void execute(Model.Command command) {
        BuiltinExecutor builtinExecutor = BuiltinExecutor.getInstance();

        String name = command.getArgs().get(1);
        String res = name + " not found";
        if (builtinExecutor.isBuiltin(name)) {
            // It's a builtin command
            res = name + " is a shell builtin";
        } else {
        // Check if it's an external command in PATH
            for (String path : System.getenv("PATH").split(":")) {
                File file = new File(path + "/" + name);
                if (file.exists() && file.canExecute()) {
                    res = name + " is " + file.getAbsolutePath();
                    break;
                }
            }

        }
        System.out.println(res);

    }
}
