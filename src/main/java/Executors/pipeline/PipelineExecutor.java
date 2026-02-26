package Executors.pipeline;

import Executors.BuiltinExecutor;
import Model.Command;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PipelineExecutor {

    private final BuiltinExecutor builtinExecutor = BuiltinExecutor.getInstance();

    public void execute(List<Command> commands) throws Exception {

        int n = commands.size();

        // 1. Create ExecutionUnits
        List<ExecutionUnit> units = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Command cmd = commands.get(i);
            String name = cmd.getArgs().get(0);
            boolean isFirst = (i == 0);
            boolean isLast = (i == n - 1);

            if (builtinExecutor.isBuiltin(name)) {
                units.add(new BuiltinUnit(cmd, isFirst, isLast));
            } else {
                units.add(new ExternalUnit(cmd, isLast));
            }
        }

        // 2. Start all units
        for (ExecutionUnit unit : units) {
            unit.start();
        }

        // 3. Wire pipes: stdout[i] -> stdin[i+1] using transfer threads
        List<Thread> pipeThreads = new ArrayList<>();

        for (int i = 0; i < n - 1; i++) {
            InputStream src = units.get(i).getStdout();
            OutputStream dst = units.get(i + 1).getStdin();

            Thread t = new Thread(() -> {
                try {
                    src.transferTo(dst);
                    dst.close();
                } catch (IOException e) {
                    // Broken pipe — expected
                }
            });
            t.start();
            pipeThreads.add(t);
        }

        // 4. If last command is builtin, pipe its stdout to System.out
        ExecutionUnit lastUnit = units.get(n - 1);
        if (lastUnit.getStdout() != null) {
            Thread lastOut = new Thread(() -> {
                try {
                    InputStream src = lastUnit.getStdout();
                    src.transferTo(System.out);
                    System.out.flush();
                } catch (IOException e) {
                    // ignore
                }
            });
            lastOut.start();
            pipeThreads.add(lastOut);
        }

        // 5. Wait for all pipe threads to finish
        for (Thread t : pipeThreads) {
            t.join();
        }

        // 6. Wait for all units to finish
        for (ExecutionUnit unit : units) {
            unit.waitFor();
        }
    }
}