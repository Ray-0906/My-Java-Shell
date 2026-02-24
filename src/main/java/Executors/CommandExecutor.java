package Executors;
import Model.Command;
public class CommandExecutor {
    private static CommandExecutor instance = null;
    private final BuiltinExecutor builtinExecutor;
    private final ExternalExecutor externalExecutor;
    
    private CommandExecutor() {
        this.builtinExecutor = BuiltinExecutor.getInstance();
        this.externalExecutor = ExternalExecutor.getInstance();
    }
   
    public static CommandExecutor getInstance() {
        if(instance == null) {
            instance = new CommandExecutor();
        }
        return instance;
    }

    public void execute(Command command) {
        if (command == null || command.getArgs().isEmpty()) return;

        String name = command.getArgs().get(0);

        if (builtinExecutor.isBuiltin(name)) {
            builtinExecutor.execute(command);
        } else {
            externalExecutor.execute(command);
        }
    }
}
