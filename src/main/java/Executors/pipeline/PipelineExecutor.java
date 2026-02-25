package Executors.pipeline;

import Model.Command;
import Executors.BuiltinExecutor;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

public class PipelineExecutor {

    private final List<Thread> pipeThreads = new ArrayList<>();

    private void pipe(InputStream in, OutputStream out) {

        if (in == null || out == null)
            return;

        Thread t = new Thread(() -> {
            try {
                in.transferTo(out);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        t.start();
        pipeThreads.add(t);
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

        // Start all processes
        for (ExecutionUnit unit : units) {
            unit.start();
        }

        // Connect pipes
        for (int i = 0; i < units.size() - 1; i++) {
            pipe(units.get(i).getStdout(), units.get(i + 1).getStdin());
        }

        // Last → terminal
        pipe(units.get(units.size() - 1).getStdout(), System.out);

        // Wait for processes
        for (ExecutionUnit unit : units) {
            unit.waitFor();
        }

        // 🔥 IMPORTANT: wait for pipe threads
        for (Thread t : pipeThreads) {
            t.join();
        }
    }
}