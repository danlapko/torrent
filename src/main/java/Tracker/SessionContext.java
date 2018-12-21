package Tracker;

import Tracker.Messages.Response;
import Tracker.Model.Catalog;
import Tracker.Model.ClientMeta;

import java.io.*;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class SessionContext {

    public final Socket socket;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;

    AtomicInteger requestCounter = new AtomicInteger(0);
    AtomicInteger responseCounter = new AtomicInteger(0);

    public final Catalog catalog;

    private final long expirationTime;
    public volatile long lastUpdate = System.currentTimeMillis();

    public final byte ip[];
    public final ReentrantLock lockClientMeta = new ReentrantLock();
    public volatile ClientMeta clientMeta = null;
    public final PriorityQueue<Response> responseQueue = new PriorityQueue<>();


    SessionContext(Catalog catalog, Socket socket, long expirationTime) throws IOException {
        this.catalog = catalog;
        this.socket = socket;
        this.expirationTime = expirationTime;
        ip = socket.getInetAddress().getAddress();

        // TODO: maybe should be in another order
        OutputStream outputStream = this.socket.getOutputStream();
        InputStream inputStream = this.socket.getInputStream();
        dataOutputStream = new DataOutputStream(outputStream);
        dataInputStream = new DataInputStream(inputStream);
    }

    boolean connectionClosed() throws IOException {
        if (socket.isClosed() || (System.currentTimeMillis() - lastUpdate > expirationTime)) {
            if(socket.isClosed()) {
                System.err.println(socket.getRemoteSocketAddress() + " socket closed");
            }
            else{
                System.err.println(socket.getRemoteSocketAddress() + " expired");
            }
            closeConnection();
            return true;
        }
        return false;
    }

    private void closeConnection() throws IOException {
        if (!socket.isClosed()) {
            System.err.println("Connection dead: " + socket.getRemoteSocketAddress().toString());
            socket.close();
        }
        lockClientMeta.lock();
        if(clientMeta!=null)
            clientMeta.removeMe();
        lockClientMeta.unlock();
    }

}

