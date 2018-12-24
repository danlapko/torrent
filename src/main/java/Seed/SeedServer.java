package Seed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SeedServer implements Runnable {

    final GlobalContext globalContext;

    SeedServer(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket(globalContext.myServerPort);

            while (true) {
                Socket socket = serverSocket.accept();
                Thread connectionThread = new Thread(new Connection(socket, globalContext));
                connectionThread.setDaemon(true);
                connectionThread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
