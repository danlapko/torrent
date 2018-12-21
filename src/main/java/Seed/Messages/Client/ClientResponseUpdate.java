package Seed.Messages.Client;

// Формат ответа: <status: Boolean>, status — True, если информация успешно обновлена

import java.io.DataInputStream;
import java.io.IOException;

public class ClientResponseUpdate extends ClientResponse {
    private final boolean status;

    public ClientResponseUpdate(DataInputStream dataInputStream) throws IOException {
        status = dataInputStream.readBoolean();
    }
}
