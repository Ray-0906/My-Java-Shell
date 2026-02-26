package Executors.pipeline;

import java.io.InputStream;
import java.io.OutputStream;

public interface ExecutionUnit {

    void start() throws Exception;

    InputStream getStdout();

    OutputStream getStdin();

    void waitFor() throws Exception;

    void destroy();
}