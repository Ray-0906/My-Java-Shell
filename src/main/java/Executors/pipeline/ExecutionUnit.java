package Executors.pipeline;



import java.io.InputStream;
import java.io.OutputStream;

public interface ExecutionUnit {
    // Set the input stream for this execution unit

    InputStream getStdout();
    // Set the output stream for this execution unit

    OutputStream getStdin();
    
    // Wait for the execution to complete (if applicable)

    void waitFor() throws Exception;
}
