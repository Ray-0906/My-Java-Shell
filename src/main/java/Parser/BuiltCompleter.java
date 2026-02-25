package Parser;

import java.io.File;
import java.util.List;

import org.jline.reader.*;


import java.util.*;

public class BuiltCompleter implements Completer {

    private String lastPrefix = null;
    private int tabCount = 0;

    @Override
    public void complete(LineReader reader,
                         ParsedLine line,
                         List<Candidate> candidates) {

        if (line.wordIndex() != 0) {
            return;
        }

        String prefix = line.word();

        List<String> matches = new ArrayList<>();

        // 1️⃣ Builtins
        for (String cmd : List.of("echo", "exit")) {
            if (cmd.startsWith(prefix)) {
                matches.add(cmd);
            }
        }

        // 2️⃣ PATH executables
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            for (String dir : pathEnv.split(":")) {
                File directory = new File(dir);
                if (!directory.exists() || !directory.isDirectory())
                    continue;

                File[] files = directory.listFiles();
                if (files == null) continue;

                for (File file : files) {
                    if (file.isFile() && file.canExecute()) {
                        String name = file.getName();
                        if (name.startsWith(prefix)) {
                            matches.add(name);
                        }
                    }
                }
            }
        }

        // Remove duplicates
        Set<String> unique = new HashSet<>(matches);
        matches = new ArrayList<>(unique);

        Collections.sort(matches);

        // ===== HANDLE MULTIPLE MATCHES =====

        if (matches.size() > 1) {

            if (prefix.equals(lastPrefix)) {
                tabCount++;
            } else {
                tabCount = 1;
            }

            lastPrefix = prefix;

            if (tabCount == 1) {
                // First TAB → bell
                reader.getTerminal().writer().print("\u0007");
                reader.getTerminal().flush();
            }
            else if (tabCount == 2) {
                // Second TAB → print matches

                reader.getTerminal().writer().println();
                reader.getTerminal().writer().println(
                        String.join("  ", matches)
                );
                reader.getTerminal().flush();

                // Reset counter after displaying
                tabCount = 0;
            }

            return;
        }

        // ===== SINGLE MATCH =====
        if (matches.size() == 1) {

            tabCount = 0;
            lastPrefix = null;

            candidates.add(new Candidate(
                    matches.get(0),
                    matches.get(0),
                    null,
                    null,
                    null,
                    null,
                    true
            ));
            return;
        }

        // ===== NO MATCH =====
        tabCount = 0;
        lastPrefix = null;

        reader.getTerminal().writer().print("\u0007");
        reader.getTerminal().flush();
    }
}