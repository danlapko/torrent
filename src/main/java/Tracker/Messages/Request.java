package Tracker.Messages;

public abstract class Request {
    public final int requestNum;

    Request(int requestNum) {
        this.requestNum = requestNum;
    }
}
