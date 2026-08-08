package Parser;


import org.jline.reader.*;

import java.util.*;


import Parser.completers.CommandCompleter;
import Parser.completers.FileCompleter;

// built complters implementing the jlines Completer interface 

public class BuiltCompleter implements Completer {

    // method overriding ( runtime polymorphism )
    // @override makes sure it checks the parent for the same signature exists 
    // methods resolved at runtime 
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