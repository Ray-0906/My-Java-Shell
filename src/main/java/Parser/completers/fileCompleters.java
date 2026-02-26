package Parser.completers;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import ShellContext.ShellContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class fileCompleters {
    private static int tabCount = 0;
    private static String lastPrefix = null;

    private static String longestCommonPrefix(List<String> strings) {
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

    public static void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        // Implement file completion logic here
        String prefix = line.word();

        // Determine the directory to search and the filename prefix
        File searchDir;
        String filePrefix;
        String pathPrefix; // the directory part to prepend to matches

        if (prefix.contains("/")) {
            int lastSlash = prefix.lastIndexOf('/');
            String dirPath = prefix.substring(0, lastSlash + 1); // includes trailing /
            filePrefix = prefix.substring(lastSlash + 1);
            pathPrefix = dirPath;

            // Resolve relative to current directory
            File dir = new File(dirPath);
            if (!dir.isAbsolute()) {
                dir = new File(ShellContext.getCurrentDir().toFile(), dirPath);
            }
            searchDir = dir;
        } else {
            searchDir = ShellContext.getCurrentDir().toFile();
            filePrefix = prefix;
            pathPrefix = "";
        }

        File[] files = searchDir.listFiles();

        if (files == null) {
            reader.getTerminal().writer().print("\u0007");
            reader.getTerminal().flush();
            return;
        }

        List<String> matches = new ArrayList<>();
        for (File f : files) {
            if (f.getName().startsWith(filePrefix)) {
                String slash=f.isDirectory() ? "/" : "";
                matches.add(pathPrefix + f.getName() + slash);
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