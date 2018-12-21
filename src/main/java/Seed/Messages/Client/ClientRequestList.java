package Seed.Messages.Client;

// Формат запроса: <1: Byte>

import Seed.Exceptions.ConnectionBrokenException;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClientRequestList extends ClientRequest {
    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws ConnectionBrokenException,IOException {
        dataOutputStream.writeByte(1);
    }
}

