package Tracker.Tasks;

import Tracker.Messages.RequestSources;
import Tracker.Messages.ResponseSources;
import Tracker.Model.ClientMeta;
import Tracker.Model.FileMeta;
import Tracker.SessionContext;

import java.util.ArrayList;
import java.util.List;

public class TaskSources implements Runnable {
    private final SessionContext context;
    private final RequestSources request;

    public TaskSources(SessionContext context, RequestSources request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public void run() {
        int id = request.id;
        FileMeta fileMeta = context.catalog.getFile(id);
        List<ClientMeta> clients = new ArrayList<>(fileMeta.getClients());
        ResponseSources response = new ResponseSources(request.requestNum, clients.size(), clients);

        System.err.println(context.socket.getRemoteSocketAddress() + " execute  <sources" + response.responseNum + "> numSources=" + response.clients.size());

        synchronized (context.responseQueue) {
            context.responseQueue.offer(response);
            context.responseQueue.notify();
        }
    }
}
