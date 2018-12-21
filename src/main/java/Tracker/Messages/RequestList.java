package Tracker.Messages;

// Формат запроса: <1: Byte>

public class RequestList extends Request {
    public RequestList(int requestNum) {
        super(requestNum);
    }
}

