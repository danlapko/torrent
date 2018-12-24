package Seed.Messages.Client;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ClientRequest {

    public abstract void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException;

}
