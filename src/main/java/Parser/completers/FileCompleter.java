package Parser.completers;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import ShellContext.ShellContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileCompleter {
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
        // line.word() returns current word being completed 
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
                String slash = f.isDirectory() ? "/" : ""; // Append a slash if it's a directory
                matches.add(pathPrefix + f.getName() + slash);
            }
        }

        Collections.sort(matches);

        // Single match — complete with trailing space
        if (matches.size() == 1) {
            tabCount = 0;
            lastPrefix = null;
            boolean dir = matches.get(0).endsWith("/");
            candidates.add(new Candidate(
                    matches.get(0),
                    matches.get(0),
                    null, null, null, null,
                    !dir)); // if it's a directory, don't add a space after completion
            return;
        }

        // No match — bell
        if (matches.isEmpty()) {
            tabCount = 0;
            lastPrefix = null;
            reader.getTerminal().writer().print("\u0007"); // this is to play a beep sound on terminal to indicate that there are no matches
            reader.getTerminal().flush();
            return;
        }

        // Multiple matches — same double-tab behavior as commands
        String lcp = longestCommonPrefix(matches);

        if (lcp.length() > prefix.length()) { 
        // If we can extend the prefix → complete it (all the matches share a common prefix longer than the current prefix)
            candidates.add(new Candidate(
                    lcp, lcp, null, null, null, null, false));  
            tabCount = 0;
            lastPrefix = null;
            return;
        }

        if (prefix.equals(lastPrefix)) {
            tabCount++; // happens only for the second tab press without changing the prefix, so tabCount will be 2 here
        } else {
            tabCount = 1;
        }

        lastPrefix = prefix;

        if (tabCount == 1) {
            reader.getTerminal().writer().print("\u0007");  // bell sound to indicate multiple matches
            reader.getTerminal().flush();  
        } else if (tabCount == 2) {
            // Print all matches on a new line, separated by two spaces on new line, and then redraw the input line
            reader.getTerminal().writer().println();
            reader.getTerminal().writer()
                    .println(String.join("  ", matches));
            reader.getTerminal().flush();
                 // Redraw the input line after printing matches
            reader.callWidget(LineReader.REDRAW_LINE);
            reader.callWidget(LineReader.REDISPLAY);

            tabCount = 0;
        }
    }

}