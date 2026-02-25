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
        int wordIndex = line.wordIndex();

        // Only autocomplete first word
        if (wordIndex == 0) {
            for (String cmd : BUILTINS) {
                if (cmd.startsWith(word)) {
                    candidates.add(new Candidate(cmd + " "));
                }
            }
        }
    }
}
