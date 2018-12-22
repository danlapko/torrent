package Seed.Messages.Client;

// Формат запроса: <2: Byte> <id: Int> <part: Int> id — идентификатор файла, part — номер части

import java.io.DataOutputStream;
import java.io.IOException;

public class ClientRequestGet extends ClientRequest {
    public final int id;
    public final int part;

    public ClientRequestGet(int id, int part) throws IOException {
        this.id = id;
        this.part = part;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(2);
        dataOutputStream.writeInt(id);
        dataOutputStream.writeInt(part);
        dataOutputStream.flush();
    }
}
