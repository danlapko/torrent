package Seed.Messages.Client;

import Seed.Model.ClientMeta;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

// Формат ответа: <size: Int> (<ip: ByteByteByteByte> <port: Short>)*,
// size — количество клиентов, раздающих файл ip — ip клиента,
public class ClientResponseSources extends ClientResponse {
    private final int size;
    public final ArrayList<ClientMeta> clients;

    public ClientResponseSources(DataInputStream dataInputStream) throws IOException {
        size = dataInputStream.readInt();
        clients = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {

            byte ip[] = new byte[4];
            dataInputStream.readFully(ip);
            short port = dataInputStream.readShort();
            ClientMeta clientMeta = new ClientMeta(ip, port);
            clients.add(i, clientMeta);
        }
    }
}
