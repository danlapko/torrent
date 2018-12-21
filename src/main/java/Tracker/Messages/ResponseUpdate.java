package Tracker.Messages;

// Формат ответа: <status: Boolean>, status — True, если информация успешно обновлена

import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseUpdate extends Response {
    private final boolean status;

    public ResponseUpdate(int responseNum, boolean status) {
        super(responseNum);
        this.status = status;
    }

    @Override
    public void writeToDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBoolean(status);
    }
}
