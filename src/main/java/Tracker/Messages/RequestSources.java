package Tracker.Messages;


import java.io.DataInputStream;
import java.io.IOException;

// Формат запроса: <3: Byte> <id: Int>, id — идентификатор файла

public class RequestSources extends Request {
    public final int id;
    public RequestSources(int requestNum, DataInputStream dataInputStream) throws IOException {
        super(requestNum);
        id = dataInputStream.readInt();
    }
}
