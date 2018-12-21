package Tracker.Tasks;

import Tracker.Messages.RequestUpload;
import Tracker.Messages.ResponseUpload;
import Tracker.Model.FileMeta;
import Tracker.SessionContext;

public class TaskUpload implements Runnable {
    private final SessionContext context;
    private final RequestUpload request;

    public TaskUpload(SessionContext context, RequestUpload request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public void run() {
        FileMeta file = context.catalog.createAndAdd(request.name, request.size);
        ResponseUpload response = new ResponseUpload(request.requestNum, file.id);

        System.err.println(context.socket.getRemoteSocketAddress() + " execute  <upload" + request.requestNum + ">");

        synchronized (context.responseQueue) {
            context.responseQueue.offer(response);
            context.responseQueue.notify();
        }
    }
}
