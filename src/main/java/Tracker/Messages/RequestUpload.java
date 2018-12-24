package Tracker.Messages;

import java.io.DataInputStream;
import java.io.IOException;

// Формат запроса: <2: Byte> <name: String> <size: Long>, name — название файла, size — размер файла

public class RequestUpload extends Request {
    public final String name;
    public final long size;

    public RequestUpload(int requestNum, DataInputStream dataInputStream) throws IOException {
        super(requestNum);
        name = dataInputStream.readUTF();
        size = dataInputStream.readLong();
    }
}
