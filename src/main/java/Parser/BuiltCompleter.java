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

        // Word index 0 = command name (first word)
        if (line.wordIndex() == 0) {
            String prefix = line.word();

            List<String> matches = new ArrayList<>();

            // 1️⃣ Builtins
            for (String cmd : List.of("echo", "type", "history", "cd", "exit")) {
                if (cmd.startsWith(prefix)) {
                    matches.add(cmd);
                }
            }

            // 2️⃣ Executables in PATH
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

                for (String match : matches) {
                    candidates.add(new Candidate(match, match, null, null, null, null, true));
                }
            }
        }
    }
}