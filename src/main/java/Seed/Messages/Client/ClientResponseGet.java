package Seed.Messages.Client;

import Seed.Messages.Server.ServerResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Формат ответа: <contentSize: Int><content: Bytes>, content — содержимое части

public class ClientResponseGet extends ClientResponse {

    public final int contentSize;
    public final byte[] content;

    public ClientResponseGet(DataInputStream dataInputStream) throws IOException {
        this.contentSize = dataInputStream.readInt();
        this.content = new byte[contentSize];
        dataInputStream.read(content);
    }

}
