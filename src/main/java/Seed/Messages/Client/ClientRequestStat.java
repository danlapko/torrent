package Seed.Messages.Client;


import java.io.DataOutputStream;
import java.io.IOException;

// Stat:
// Формат запроса: <1: Byte> <id: Int>, id — идентификатор файла

public class ClientRequestStat extends ClientRequest {
    public final int id;

    public ClientRequestStat(int id) throws IOException {
        this.id = id;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(id);
    }
}
