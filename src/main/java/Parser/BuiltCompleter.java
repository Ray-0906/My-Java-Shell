package Parser;

import java.util.List;

import org.jline.reader.Completer;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class BuiltCompleter implements Completer {
    private static final List<String> BUILTINS = List.of("echo", "cd", "pwd", "type", "exit");

    @Override
    public void complete(LineReader reader,
            ParsedLine line,
            List<Candidate> candidates) {

        String word = line.word();

        if (line.wordIndex() == 0) {
            for (String cmd : List.of("echo", "exit")) {
                if (cmd.startsWith(word)) {
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

            // 🔔 If no matches found, ring bell
            if (candidates.isEmpty()) {
                reader.getTerminal().writer().print("\u0007");
                reader.getTerminal().flush();
            }
        }
    }
}
