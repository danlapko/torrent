package Tracker;

import Tracker.Model.Catalog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tracker {
    public static void main(String[] args) throws IOException {
        int port = 8081;
        int numWorkers = 4;
//        long expirationTime = 6 * 60 * 1000;
        long expirationTime =  10 * 1000;


        Catalog catalog = new Catalog();

        ServerSocket server = new ServerSocket(port);
        ExecutorService threadPool = Executors.newFixedThreadPool(numWorkers);

        while (true) {
            Socket socket = server.accept();

            System.err.println("New connection: " + socket.getRemoteSocketAddress().toString());
            SessionContext context = new SessionContext(catalog, socket, expirationTime);
            Thread readerThread = new Thread(new Reader(context, threadPool));
            Thread writerThread = new Thread(new Writer(context));
            readerThread.setDaemon(true);
            writerThread.setDaemon(true);
            writerThread.start();
            readerThread.start();
        }

    }
}





