package Executors.pipeline;

import Executors.BuiltinExecutor;
import Model.Command;
import ShellContext.ShellContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PipelineExecutor {

    private final BuiltinExecutor builtinExecutor = BuiltinExecutor.getInstance();

    public void execute(List<Command> commands) throws Exception {

        // Check if any command is a builtin
        boolean hasBuiltin = false;
        for (Command cmd : commands) {
            if (builtinExecutor.isBuiltin(cmd.getArgs().get(0))) {
                hasBuiltin = true;
                break;
            }
        }

        if (!hasBuiltin) {
            executeAllExternal(commands);
        } else {
            executeWithBuiltins(commands);
        }
    }

    private void executeAllExternal(List<Command> commands) throws Exception {
        List<ProcessBuilder> builders = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {
            Command cmd = commands.get(i);
            ProcessBuilder pb = new ProcessBuilder(cmd.getArgs());
            pb.directory(ShellContext.getCurrentDir().toFile());

            if (i == commands.size() - 1) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }

            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            builders.add(pb);
        }

        List<Process> processes = ProcessBuilder.startPipeline(builders);

        // Wait for last process first
        processes.get(processes.size() - 1).waitFor();

        // Destroy any remaining (e.g., tail -f)
        for (int i = 0; i < processes.size() - 1; i++) {
            processes.get(i).destroyForcibly();
        }

        for (Process p : processes) {
            p.waitFor();
        }
    }

    private void executeWithBuiltins(List<Command> commands) throws Exception {

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

        // 3. Wire pipes: stdout[i] -> stdin[i+1]
        List<Thread> pipeThreads = new ArrayList<>();

        for (int i = 0; i < n - 1; i++) {
            InputStream src = units.get(i).getStdout();
            OutputStream dst = units.get(i + 1).getStdin();

            Thread t = new Thread(() -> {
                try {
                    src.transferTo(dst);  // this line set the pipe prevStdout -> nextStdin   and the thread keeps doing it 
                    dst.close();  // when the src closes with EOF .. then dst has know way to know so.. it must close otherwise it cause the next thread/process to hang indefinately
                } catch (IOException e) {
                    // Broken pipe — expected
                }
            });
            t.start();
            pipeThreads.add(t);
        }
        // mechanism is downstreams stdin closes when  upstreams terminates .. and as the process/threads are blocking by io keeping the whole flow syncronized
        // even though each threads run concurrently it stays blocks until input is rcved from upstream process/thread 
        // and upstream process/thread stays block due to pipe(buffer) full if no one is consuming from the pipe ..
        // this makes the sure the correct sequencing in the concurrent processes 
        // 4. If last is builtin, pipe stdout to System.out
        ExecutionUnit lastUnit = units.get(n - 1);
        if (lastUnit.getStdout() != null) {
            Thread lastOut = new Thread(() -> {
                try {
                    InputStream src = lastUnit.getStdout();
                    src.transferTo(System.out);  // 
                    System.out.flush();
                } catch (IOException e) {
                    // ignore
                }
            });
            lastOut.start();
            pipeThreads.add(lastOut);
        }

        // 5. Wait for last unit first
        lastUnit.waitFor();

        // 6. Destroy upstream units
        for (int i = 0; i < n - 1; i++) {
            units.get(i).destroy();
        }

        // 7. Wait for pipe threads
        for (Thread t : pipeThreads) {
            t.join(2000);
        }

        // 8. Wait for remaining units
        for (int i = 0; i < n - 1; i++) {
            units.get(i).waitFor();
        }
    }
}