package Seed.Messages.Client;

// Формат ответа: <id: Int>, id — идентификатор файла

import java.io.DataInputStream;
import java.io.IOException;

public class ClientResponseUpload extends ClientResponse {
    public final int id;

    public ClientResponseUpload(DataInputStream dataInputStream) throws IOException {
        id = dataInputStream.readInt();
    }
}
