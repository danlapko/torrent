package Seed.Messages.Server;


import java.io.DataInputStream;
import java.io.IOException;

// Stat:
// Формат запроса: <1: Byte> <id: Int>, id — идентификатор файла

public class ServerRequestStat extends ServerRequest {
    public final int id;


    public ServerRequestStat(DataInputStream dataInputStream) throws IOException {
        id = dataInputStream.readInt();
    }
}
