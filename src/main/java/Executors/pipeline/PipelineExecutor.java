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
        for (Command cmd : commands) {
            String name = cmd.getArgs().get(0);
            if (builtinExecutor.isBuiltin(name)) {
                units.add(new BuiltinUnit(cmd));
            } else {
                units.add(new ExternalUnit(cmd));
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
                    // Broken pipe — expected if downstream exits early
                }
            });
            t.start();
            pipeThreads.add(t);
        }

        // 4. Last command's stdout -> System.out
        Thread lastOut = new Thread(() -> {
            try {
                InputStream src = units.get(n - 1).getStdout();
                src.transferTo(System.out);
                System.out.flush();
            } catch (IOException e) {
                // ignore
            }
        });
        lastOut.start();
        pipeThreads.add(lastOut);

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