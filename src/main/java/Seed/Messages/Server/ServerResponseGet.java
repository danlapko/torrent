package Seed.Messages.Server;

import java.io.DataOutputStream;
import java.io.IOException;

// Формат ответа: <contentSize: Int><content: Bytes>, content — содержимое части

public class ServerResponseGet extends ServerResponse {

    final int contentSize;
    final byte[] content;

    public ServerResponseGet(int contentSize, byte[] content) {
        this.contentSize = contentSize;
        this.content = content;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(contentSize);
        dataOutputStream.write(content);
    }
}
