package Tracker.Messages;

import java.io.DataInputStream;
import java.io.IOException;

// Формат запроса: <4: Byte> <port: Short> <count: Int> (<id: Int>)*
// port — порт клиента, count — количество раздаваемых файлов, id — идентификатор файла


public class RequestUpdate extends Request {
    public final short port;
    public final int count;
    public final int[] ids;

    public RequestUpdate(int requestNum, DataInputStream dataInputStream) throws IOException {
        super(requestNum);
        port = dataInputStream.readShort();
        count = dataInputStream.readInt();
        ids = new int[count];
        for (int i = 0; i < count; i++) {
            ids[i] = dataInputStream.readInt();
        }

    }

}
