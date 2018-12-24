package Seed.Messages.Client;


import java.io.DataOutputStream;
import java.io.IOException;

// Формат запроса: <3: Byte> <id: Int>, id — идентификатор файла

public class ClientRequestSources extends ClientRequest {
    public final int id;

    public ClientRequestSources(int id) throws IOException {
        this.id = id;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(3);
        dataOutputStream.writeInt(id);
        dataOutputStream.flush();
    }
}
