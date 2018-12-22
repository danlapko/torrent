package Seed.Messages.Client;

// Формат ответа: <count: Int> (<id: Int> <name: String> <size: Long>)*,
// count — количество файлов id — идентификатор файла name — название файла size — размер файла

import Seed.Model.FileMeta;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ClientResponseList extends ClientResponse {
    private final int count;
    public final ArrayList<FileMeta> files;

    public ClientResponseList(DataInputStream dataInputStream) throws IOException {

        count = dataInputStream.readInt();
        files = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int fileId = dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            long size = dataInputStream.readLong();
            FileMeta fileMeta = new FileMeta(fileId, name, size);
            files.add(i, fileMeta);
        }
    }

}
