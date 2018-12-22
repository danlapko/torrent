package Seed.Messages.Server;

import java.io.DataOutputStream;
import java.io.IOException;

// Формат ответа: <count: Int> (<part: Int>)*, count — количество доступных частей, part — номер части

public class ServerResponseStat extends ServerResponse {
    final int count;
    public final int[] parts;

    public ServerResponseStat(int count, int[] parts) {

        this.count = count;
        this.parts = parts;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(count);
        for (int i = 0; i < count; i++) {
            dataOutputStream.writeInt(parts[i]);
        }
        dataOutputStream.flush();
    }
}
