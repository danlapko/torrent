package Tracker.Messages;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Response {
    public final int responseNum;

    Response(int responseNum) {
        this.responseNum = responseNum;
    }

    public abstract void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException;
}