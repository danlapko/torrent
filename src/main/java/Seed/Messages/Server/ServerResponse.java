package Seed.Messages.Server;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ServerResponse {

    public abstract void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException;
}