package Parser;

import java.io.File;

import org.jline.reader.*;

import java.util.*;

import ShellContext.ShellContext;

public class BuiltCompleter implements Completer {

    private String lastPrefix = null;
    private int tabCount = 0;

    private String longestCommonPrefix(List<String> strings) {
        if (strings == null || strings.isEmpty())
            return "";

        String prefix = strings.get(0);

        for (int i = 1; i < strings.size(); i++) {
            while (!strings.get(i).startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty())
                    return "";
            }
        }

        return prefix;
    }

    @Override
    public void complete(LineReader reader,
            ParsedLine line,
            List<Candidate> candidates) {

        // Argument position — filename completion
        if (line.wordIndex() != 0) {
            completeFilename(reader, line, candidates);
            return;
        }

        // Command position — existing logic unchanged
        String prefix = line.word();

        List<String> matches = new ArrayList<>();

        // 1️⃣ Builtins
        for (String cmd : List.of("echo", "type", "history", "cd", "exit")) {
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
                if (files == null)
                    continue;

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

            String lcp = longestCommonPrefix(matches);

            // If we can extend the prefix → complete it
            if (lcp.length() > prefix.length()) {

                candidates.add(new Candidate(
                        lcp,
                        lcp,
                        null,
                        null,
                        null,
                        null,
                        false // IMPORTANT: no trailing space
                ));

                tabCount = 0;
                lastPrefix = null;
                return;
            }

            // If no extension possible → fallback to double-tab behavior
            if (prefix.equals(lastPrefix)) {
                tabCount++;
            } else {
                tabCount = 1;
            }

            lastPrefix = prefix;

            if (tabCount == 1) {
                reader.getTerminal().writer().print("\u0007");
                reader.getTerminal().flush();
            } else if (tabCount == 2) {

                reader.getTerminal().writer().println();
                reader.getTerminal().writer()
                        .println(String.join("  ", matches));
                reader.getTerminal().flush();

                reader.callWidget(LineReader.REDRAW_LINE);
                reader.callWidget(LineReader.REDISPLAY);

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
                    true));
            return;
        }

        // ===== NO MATCH =====
        tabCount = 0;
        lastPrefix = null;

        reader.getTerminal().writer().print("\u0007");
        reader.getTerminal().flush();
    }

    private void completeFilename(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String prefix = line.word();

        File currentDir = ShellContext.getCurrentDir().toFile();
        File[] files = currentDir.listFiles();

        if (files == null) {
            reader.getTerminal().writer().print("\u0007");
            reader.getTerminal().flush();
            return;
        }

        List<String> matches = new ArrayList<>();
        for (File f : files) {
            if (f.getName().startsWith(prefix)) {
                matches.add(f.getName());
            }
        }

        Collections.sort(matches);

        // Single match — complete with trailing space
        if (matches.size() == 1) {
            tabCount = 0;
            lastPrefix = null;
            candidates.add(new Candidate(
                    matches.get(0),
                    matches.get(0),
                    null, null, null, null,
                    true));
            return;
        }

        // No match — bell
        if (matches.isEmpty()) {
            tabCount = 0;
            lastPrefix = null;
            reader.getTerminal().writer().print("\u0007");
            reader.getTerminal().flush();
            return;
        }

        // Multiple matches — same double-tab behavior as commands
        String lcp = longestCommonPrefix(matches);

        if (lcp.length() > prefix.length()) {
            candidates.add(new Candidate(
                    lcp, lcp, null, null, null, null, false));
            tabCount = 0;
            lastPrefix = null;
            return;
        }

        if (prefix.equals(lastPrefix)) {
            tabCount++;
        } else {
            tabCount = 1;
        }

        lastPrefix = prefix;

        if (tabCount == 1) {
            reader.getTerminal().writer().print("\u0007");
            reader.getTerminal().flush();
        } else if (tabCount == 2) {
            reader.getTerminal().writer().println();
            reader.getTerminal().writer()
                    .println(String.join("  ", matches));
            reader.getTerminal().flush();

            reader.callWidget(LineReader.REDRAW_LINE);
            reader.callWidget(LineReader.REDISPLAY);

            tabCount = 0;
        }
    }
}