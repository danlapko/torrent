package Seed.Messages.Client;

import Seed.Messages.Server.ServerResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Формат ответа: <count: Int> (<part: Int>)*, count — количество доступных частей, part — номер части

public class ClientResponseStat extends ClientResponse {
    public final int count;
    public final int[] parts;

    public ClientResponseStat(DataInputStream dataInputStream) throws IOException {

        this.count = dataInputStream.readInt();
        this.parts = new int[count];
        for (int i = 0; i < count; i++) {
            parts[i] = dataInputStream.readInt();
        }
    }

}
