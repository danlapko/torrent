package Tracker.Messages;

// Формат ответа: <id: Int>, id — идентификатор файла

import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseUpload extends Response {
    private final int id;

    public ResponseUpload(int responseNum, int id) {
        super(responseNum);
        this.id = id;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(id);
    }
}
