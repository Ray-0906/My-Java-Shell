package Executors.pipeline;

import Model.Command;
import ShellContext.ShellContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PipelineExecutor {

    public void execute(List<Command> commands) throws Exception {

        List<ProcessBuilder> builders = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {

            Command cmd = commands.get(i);

            ProcessBuilder pb = new ProcessBuilder(cmd.getArgs());
            pb.directory(ShellContext.getCurrentDir().toFile());

            // Only last command outputs to terminal
            if (i == commands.size() - 1) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }

            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            builders.add(pb);
        }

        // 🔥 THIS handles the entire pipeline
        List<Process> processes = ProcessBuilder.startPipeline(builders);

        // Wait for all
        for (Process p : processes) {
            p.waitFor();
        }
    }
}