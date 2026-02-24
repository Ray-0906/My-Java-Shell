package Executors;
import Model.Command;
import java.util.List;
import Builtins.CdCommand;
import Builtins.EchoCommand;
import Builtins.PwdCommand;
import Builtins.TypeCommand;

public class BuiltinExecutor {
    // Singleton pattern to ensure only one instance of BuiltinExecutor exists
    private static BuiltinExecutor instance;
    private BuiltinExecutor() {}
    public static BuiltinExecutor getInstance() {
        if (instance == null) {
            instance = new BuiltinExecutor();
        }
        return instance;
    }
    
    public boolean isBuiltin(String name) {
        return List.of("echo", "cd", "pwd", "type", "exit").contains(name);
    }

    public void execute(Command command) {
        String name = command.getArgs().get(0);

        switch (name) {
            case "echo" -> EchoCommand.execute(command);
            case "cd"   -> CdCommand.execute(command);
            case "pwd"  -> PwdCommand.execute(command);
            case "type" -> TypeCommand.execute(command);
            case "exit" -> System.exit(0);
        }
    }
}
