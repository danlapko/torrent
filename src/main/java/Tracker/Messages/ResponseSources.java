package Tracker.Messages;

import Tracker.Model.ClientMeta;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Формат ответа: <size: Int> (<ip: ByteByteByteByte> <port: Short>)*,
// size — количество клиентов, раздающих файл ip — ip клиента,
public class ResponseSources extends Response {
    private final int size;
    public final List<ClientMeta> clients;

    public ResponseSources(int responseNum, int size, List<ClientMeta> clients) {
        super(responseNum);
        this.size = size;
        this.clients = clients;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(size);
        for (ClientMeta client : clients) {
            dataOutputStream.write(client.ip);
            dataOutputStream.writeShort(client.port);
        }
        dataOutputStream.flush();
    }
}
