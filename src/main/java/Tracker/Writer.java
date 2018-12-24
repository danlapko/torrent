package Tracker;

import Tracker.Messages.Response;

import java.io.IOException;

class Writer implements Runnable {
    private final SessionContext context;

    Writer(SessionContext context) {
        this.context = context;
    }

    @Override
    public void run() {


        synchronized (context.responseQueue) {
            try {

                while (true) {
                    context.responseQueue.wait();
                    Response headResponse = context.responseQueue.peek();

                    if (context.connectionClosed()) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    while (headResponse != null
                            && context.responseCounter.compareAndSet(headResponse.responseNum, headResponse.responseNum + 1)) {
                        headResponse = context.responseQueue.poll();

                        System.err.println(context.socket.getRemoteSocketAddress().toString() + " response <" + headResponse.responseNum + "> ");

                        headResponse.writeToDataOutputStream(context.dataOutputStream);
                        headResponse = context.responseQueue.peek();
                    }

                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

    }
}
