package Executors.pipeline;

import Model.Command;
import Executors.BuiltinExecutor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
public class PipelineExecutor {

    private void pipe(InputStream in, OutputStream out) {

        if (in == null || out == null) return;

        new Thread(() -> {
            try (in; out) {
                in.transferTo(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void execute(List<Command> commands) throws Exception {
        List<ExecutionUnit> units = new ArrayList<>();

        for (Command command : commands) {
            if (BuiltinExecutor.getInstance().isBuiltin(command.getArgs().get(0))) {
                units.add(new BuiltinUnit(command));
            } else {
                units.add(new ExternalUnit(command));
            }
        }

        for (int i = 0; i < units.size() - 1; i++) {
            pipe(units.get(i).getStdout(), units.get(i + 1).getStdin());
        }

        pipe(
            units.get(units.size() - 1).getStdout(),
            System.out
        );
        

        for (ExecutionUnit unit : units) {
            unit.waitFor();
        }
    }
}
