package Parser;

import java.io.File;
import java.util.List;

import org.jline.reader.Completer;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class BuiltCompleter implements Completer {
    private static final List<String> BUILTINS = List.of("echo", "cd", "pwd", "type", "exit");

    List<String> getBuiltins() {
        return BUILTINS;
    }

    @Override
    public void complete(LineReader reader,
            ParsedLine line,
            List<Candidate> candidates) {

        String prefix = line.word();

        if (line.wordIndex() != 0) {
            return; // only complete command name
        }

        // 1️⃣ Builtins
        for (String cmd : List.of("echo", "exit")) {
            if (cmd.startsWith(prefix)) {
                candidates.add(new Candidate(
                        cmd,
                        cmd,
                        null,
                        null,
                        null,
                        null,
                        true));
            }
        }

        // 2️⃣ External executables from PATH
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {

            String[] dirs = pathEnv.split(":");

            for (String dir : dirs) {

                File directory = new File(dir);

                if (!directory.exists() || !directory.isDirectory()) {
                    continue; // skip invalid PATH entries
                }

                File[] files = directory.listFiles();
                if (files == null)
                    continue;

                for (File file : files) {
                    String name = file.getName();

                    if (file.isFile()
                            && file.canExecute()
                            && name.startsWith(prefix)) {

                        candidates.add(new Candidate(
                                name,
                                name,
                                null,
                                null,
                                null,
                                null,
                                true));
                    }
                }
            }
        }

        // 3️⃣ If nothing found → bell
        if (candidates.isEmpty()) {
            reader.getTerminal().writer().print("\u0007");
            reader.getTerminal().flush();
        }
    }
}
