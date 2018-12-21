package Seed.Messages.Client;

import java.io.DataOutputStream;
import java.io.IOException;

// Формат запроса: <4: Byte> <port: Short> <count: Int> (<id: Int>)*
// port — порт клиента, count — количество раздаваемых файлов, id — идентификатор файла


public class ClientRequestUpdate extends ClientRequest {
    public final short port;
    public final int count;
    public final int[] ids;

    public ClientRequestUpdate(short port, int count, int[] ids) throws IOException {
        this.port = port;
        this.count = count;
        this.ids = ids;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(4);
        dataOutputStream.writeShort(port);
        dataOutputStream.writeInt(count);
        for (int i = 0; i < count; i++) {
            dataOutputStream.writeInt(ids[i]);
        }
    }
}
