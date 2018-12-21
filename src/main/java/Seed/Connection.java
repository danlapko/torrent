package Seed;

import Seed.Messages.Server.ServerRequestGet;
import Seed.Messages.Server.ServerRequestStat;
import Seed.Messages.Server.ServerResponseGet;
import Seed.Messages.Server.ServerResponseStat;
import Seed.Model.BlockMeta;
import Seed.Model.FileMeta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection implements Runnable {
    final Socket socket;
    final GlobalContext globalContext;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;

    public Connection(Socket socket, GlobalContext globalContext) throws IOException {
        this.socket = socket;
        this.globalContext = globalContext;
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

    }


    @Override
    public void run() {
        try {
            byte type = dataInputStream.readByte();
            switch (type) {

                case 1: { // stat

                    ServerRequestStat request = new ServerRequestStat(dataInputStream);
                    System.err.println(socket.getRemoteSocketAddress().toString() + " requested myserver <stat> fileId=" + request.id);

                    FileMeta fileMeta = globalContext.catalog.getFile(request.id);
                    ServerResponseStat response;
                    if (fileMeta == null) {
                        response = new ServerResponseStat(0, new int[0]);
                    } else {
                        response = new ServerResponseStat(fileMeta.getNumBlocks(), fileMeta.getBlockIds());
                    }
                    response.writeToDataOutputStream(dataOutputStream);
                    System.err.println(socket.getRemoteSocketAddress().toString() + " responsed myserver <stat> parts=" + response.parts.toString());

                    break;
                }

                case 2: { // get
                    ServerRequestGet request = new ServerRequestGet(dataInputStream);
                    System.err.println(socket.getRemoteSocketAddress().toString() + " requested myserver <get> fileId=" + request.id + " blockId=" + request.part);

                    ServerResponseGet response;
                    FileMeta file = globalContext.catalog.getFile(request.id);
                    BlockMeta block = null;
                    if (file != null) {
                        block = file.getBlock(request.part);
                    }
                    if (block == null) {
                        response = new ServerResponseGet(0, new byte[0]);
                    } else {
                        response = new ServerResponseGet(block.size, block.data);
                    }

                    response.writeToDataOutputStream(dataOutputStream);
                    System.err.println(socket.getRemoteSocketAddress().toString() + " responsed myserver <get> " );

                    break;
                }
                default:
                    throw new RuntimeException(" invalid request type:" + type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
