package Tracker.Tasks;

import Tracker.Messages.RequestUpdate;
import Tracker.Messages.Response;
import Tracker.Messages.ResponseUpdate;
import Tracker.Model.ClientMeta;
import Tracker.Model.FileMeta;
import Tracker.SessionContext;

public class TaskUpdate implements Runnable {
    private final SessionContext context;
    private final RequestUpdate request;

    public TaskUpdate(SessionContext context, RequestUpdate request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public void run() {
        context.lockClientMeta.lock();

        if (context.clientMeta != null) {
            context.clientMeta.removeMe();
        }

        context.clientMeta = new ClientMeta(context.ip, request.port);

        for (int i = 0; i < request.count; i++) {
            int id = request.ids[i];
            FileMeta fileMeta = context.catalog.getFile(id); // fail NPE because we don't serialize/deserialize server catalog
            if (fileMeta == null) {
//                throw new RuntimeException("uploaded not available id:"+id);
            }

            fileMeta.addClient(context.clientMeta);
            context.clientMeta.addFile(fileMeta);
        }

        context.lastUpdate = System.currentTimeMillis();

        String host = Byte.toUnsignedInt(context.ip[0]) + "." + Byte.toUnsignedInt(context.ip[1]) + "." + Byte.toUnsignedInt(context.ip[2]) + "." +
                Byte.toUnsignedInt(context.ip[3]);
        System.err.println(context.socket.getRemoteSocketAddress() + " execute  <update " + host
                + ":" + request.port + " " + request.requestNum + ">");

        Response response = new ResponseUpdate(request.requestNum, true);
        synchronized (context.responseQueue) {
            context.responseQueue.offer(response);
            context.responseQueue.notify();
        }

        context.lockClientMeta.unlock();
    }
}
