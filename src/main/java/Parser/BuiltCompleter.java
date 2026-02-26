package Parser;


import org.jline.reader.*;

import java.util.*;


import Parser.completers.CommandCompleter;
import Parser.completers.FileCompleter;

public class BuiltCompleter implements Completer {

    @Override
    public void complete(LineReader reader,
            ParsedLine line,
            List<Candidate> candidates) {

        // Argument position — filename completion
        if (line.wordIndex() != 0) {
            FileCompleter.complete(reader, line, candidates);
            return;
        }
        
        CommandCompleter.complete(reader, line, candidates);

     
    }

}