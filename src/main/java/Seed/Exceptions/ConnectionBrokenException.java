package Seed.Exceptions;

import java.io.EOFException;
import java.io.IOException;

public class ConnectionBrokenException extends IOException {
    public ConnectionBrokenException(IOException e) {
        super(e);
    }
}
