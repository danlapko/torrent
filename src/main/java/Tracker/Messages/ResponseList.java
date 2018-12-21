package Tracker.Messages;

// Формат ответа: <count: Int> (<id: Int> <name: String> <size: Long>)*,
// count — количество файлов id — идентификатор файла name — название файла size — размер файла

import Tracker.Model.FileMeta;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ResponseList extends Response {
    private final int count;
    private final List<FileMeta> files;

    public ResponseList(int responseNum, int count, List<FileMeta> files) {
        super(responseNum);
        this.count = count;
        this.files = files;
    }

    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeInt(count);
        for (FileMeta fileMeta : files) {
            dataOutputStream.writeInt(fileMeta.id);
            dataOutputStream.writeUTF(fileMeta.name);
            dataOutputStream.writeLong(fileMeta.size);
        }
        dataOutputStream.flush();
    }
}
