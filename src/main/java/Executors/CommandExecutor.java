package Executors;

import Executors.pipeline.PipelineExecutor;
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
        if (instance == null) {
            instance = new CommandExecutor();
        }
        return instance;
    }

    public void executeSingle(Command command) {
        // System.err.println("came at single command executor with " + command.getArgs());
         if (command == null ||  command.getArgs() == null || command.getArgs().isEmpty()) {
            //  System.out.println(" inside Single command executor with empty command");
            return;
         }

        String name = command.getArgs().get(0);

        if (builtinExecutor.isBuiltin(name)) {
            builtinExecutor.execute(command);
        } else {
            externalExecutor.execute(command);
        }
    }

    public void execute(Command command) throws Exception {

        if (command.isPipeline()) {
            // System.err.println("Executing pipeline ");
           
            new PipelineExecutor().execute(
                    command.getPipelineCommands());
        } else {
            
            executeSingle(command);
        }
    }

}
