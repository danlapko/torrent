package Tracker.Tasks;

import Tracker.Messages.RequestList;
import Tracker.Messages.ResponseList;
import Tracker.Model.FileMeta;
import Tracker.SessionContext;

import java.util.List;

public class TaskList implements Runnable {
    private final SessionContext context;
    private final RequestList request;

    public TaskList(SessionContext context, RequestList request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public void run() {
        List<FileMeta> files = context.catalog.getFiles();
        ResponseList response = new ResponseList(request.requestNum, files.size(), files);

        System.err.println(context.socket.getRemoteSocketAddress() + " execute  <list" + request.requestNum + ">");

        synchronized (context.responseQueue) {
            context.responseQueue.offer(response);
            context.responseQueue.notify();
        }

    }
}
