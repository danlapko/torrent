package Seed.Messages.Client;

import java.io.DataOutputStream;
import java.io.IOException;

// Формат запроса: <2: Byte> <name: String> <size: Long>, name — название файла, size — размер файла

public class ClientRequestUpload extends ClientRequest {
    public final String name;
    public final long size;

    public ClientRequestUpload(String name, long size) throws IOException {
        this.name = name;
        this.size = size;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(2);
        dataOutputStream.writeUTF(name);
        dataOutputStream.writeLong(size);
        dataOutputStream.flush();
    }
}
