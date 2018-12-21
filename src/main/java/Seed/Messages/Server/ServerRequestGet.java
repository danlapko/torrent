package Seed.Messages.Server;

// Формат запроса: <2: Byte> <id: Int> <part: Int> id — идентификатор файла, part — номер части

import java.io.DataInputStream;
import java.io.IOException;

public class ServerRequestGet extends ServerRequest {
    public final int id;
    public final int part;

    public ServerRequestGet(DataInputStream dataInputStream) throws IOException {
        this.id = dataInputStream.readInt();
        this.part = dataInputStream.readInt();
    }
}
