package Tracker;

import Tracker.Messages.*;
import Tracker.Tasks.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

class Reader implements Runnable {
    private final SessionContext context;
    private final ExecutorService threadPool;

    Reader(SessionContext context, ExecutorService threadPool) {
        this.context = context;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {

        while (true) {
            try {

                if (context.connectionClosed()) {
                    Thread.currentThread().interrupt();
                    return;

                } else if (context.dataInputStream.available() == 0) {
                    continue;
                }


                byte type = context.dataInputStream.readByte();

                switch (type) {
                    case 1: {// list request
                        RequestList requestList = new RequestList(context.requestCounter.getAndIncrement());
                        System.err.println(context.socket.getRemoteSocketAddress().toString() + " request <list" + requestList.requestNum + ">");
                        threadPool.submit(new TaskList(context, requestList));
                        break;
                    }
                    case 2: { // upload request
                        RequestUpload requestUpload = new RequestUpload(context.requestCounter.getAndIncrement(), context.dataInputStream);
                        System.err.println(context.socket.getRemoteSocketAddress().toString() + " request <upload" + requestUpload.requestNum + ">");
                        threadPool.submit(new TaskUpload(context, requestUpload));
                        break;
                    }
                    case 3: { // sources request

                        RequestSources requestSources = new RequestSources(context.requestCounter.getAndIncrement(), context.dataInputStream);
                        System.err.println(context.socket.getRemoteSocketAddress().toString() + " request <sources" + requestSources.requestNum + ">");

                        threadPool.submit(new TaskSources(context, requestSources));
                        break;
                    }
                    case 4: { // update request

                        RequestUpdate requestUpdate = new RequestUpdate(context.requestCounter.getAndIncrement(), context.dataInputStream);
                        System.err.println(context.socket.getRemoteSocketAddress().toString() + " request <update" + requestUpdate.requestNum + ">");

                        threadPool.submit(new TaskUpdate(context, requestUpdate));
                        break;
                    }
                    default:
                        throw new RuntimeException("Invalid request type: " + String.valueOf(type));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}