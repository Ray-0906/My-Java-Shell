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
                if (prefix.isEmpty()) return "";
            }
        }

        return prefix;
    }

    @Override
    public void complete(LineReader reader,
            ParsedLine line,
            List<Candidate> candidates) {

        if (line.wordIndex() == 0) {
            // Command completion
            String prefix = line.word();

            List<String> matches = new ArrayList<>();

            // Builtins
            for (String cmd : ShellContext.getBuiltins()) {
                if (cmd.startsWith(prefix)) {
                    matches.add(cmd);
                }
            }

            // Executables in PATH
            String pathEnv = System.getenv("PATH");
            if (pathEnv != null) {
                for (String dir : pathEnv.split(File.pathSeparator)) {
                    File folder = new File(dir);
                    if (folder.isDirectory()) {
                        File[] files = folder.listFiles();
                        if (files != null) {
                            for (File f : files) {
                                if (f.isFile() && f.canExecute() && f.getName().startsWith(prefix)) {
                                    if (!matches.contains(f.getName())) {
                                        matches.add(f.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Collections.sort(matches);

            if (matches.isEmpty()) {
                return;
            }

            // Single match — complete with trailing space
            if (matches.size() == 1) {
                lastPrefix = null;
                tabCount = 0;
                candidates.add(new Candidate(matches.get(0), matches.get(0), null, null, null, null, true));
                return;
            }

            // Multiple matches — track tab presses
            if (prefix.equals(lastPrefix)) {
                tabCount++;
            } else {
                lastPrefix = prefix;
                tabCount = 1;
            }

            String lcp = longestCommonPrefix(matches);

            if (tabCount == 1) {
                // First TAB: complete to longest common prefix, ring bell
                if (lcp.length() > prefix.length()) {
                    // Partial completion available — complete to LCP, no trailing space
                    candidates.add(new Candidate(lcp, lcp, null, null, null, null, false));
                }
                // If LCP == prefix, no candidates → JLine rings bell
                return;
            }

            // Second TAB: show all matches
            for (String match : matches) {
                candidates.add(new Candidate(match, match, null, null, null, null, true));
            }

        } else {
            // Argument position — filename completion
            String prefix = line.word();

            File currentDir = ShellContext.getCurrentDir().toFile();
            File[] files = currentDir.listFiles();

            if (files != null) {
                List<String> matches = new ArrayList<>();
                for (File f : files) {
                    if (f.getName().startsWith(prefix)) {
                        matches.add(f.getName());
                    }
                }

                Collections.sort(matches);

                if (matches.size() == 1) {
                    candidates.add(new Candidate(matches.get(0), matches.get(0), null, null, null, null, true));
                } else {
                    for (String match : matches) {
                        candidates.add(new Candidate(match, match, null, null, null, null, true));
                    }
                }
            }
        }
    }
}