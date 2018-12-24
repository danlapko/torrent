package Seed.Commands;

import Seed.Exceptions.NotAvaliableFile;
import Seed.GlobalContext;
import Seed.Messages.Client.ClientRequestGet;
import Seed.Messages.Client.ClientRequestStat;
import Seed.Messages.Client.ClientResponseGet;
import Seed.Messages.Client.ClientResponseStat;
import Seed.Model.BlockMeta;
import Seed.Model.ClientMeta;
import Seed.Model.FileMeta;
import io.airlift.airline.Arguments;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@io.airlift.airline.Command(name = "download", description = "download file from torrent by its id")
public class CmdDownload implements Command {

    private FileMeta fileMeta;
    private GlobalContext context;
    private long numBlocks;
    private ExecutorService threadPool;

    @Arguments(description = "file id obtained from tracker", required = true)
    private int fileId;

    @Override
    public int execute(GlobalContext cntxt) throws Exception {
        context = cntxt;
        fileMeta = null;
        threadPool = Executors.newFixedThreadPool(context.numWorkers);

        List<FileMeta> files = context.getTrackerList();
        for (FileMeta file : files) {
            if (file.id == fileId) {
                fileMeta = file;
            }
        }

        if (fileMeta == null) {
            throw new NotAvaliableFile("Tracker has not file with such id: " + fileId + "!");
        }

        numBlocks = (fileMeta.size + context.blockSize - 1) / context.blockSize;

        List<ClientMeta> sources = context.getFileSources(fileId);

        List<Callable<Object>> todoList = new ArrayList<>(sources.size());

        for (ClientMeta client : sources) {
            todoList.add(() -> {
                downloadFromHost(client);
                return null;
            });
        }
        threadPool.invokeAll(todoList);
        threadPool.shutdown();

        context.catalog.addFile(fileMeta);
        context.updateMyFilesAtTracker();

        if (fileMeta.getBlockIds().length != numBlocks) {
            System.out.println("File " + fileMeta.name + " whith id=" + fileMeta.id + " have been partially downloaded! You have "
                    + fileMeta.getBlockIds().length + " from " + numBlocks + " blocks");
        } else {
            System.out.println("File " + fileMeta.name + " whith id=" + fileMeta.id + " have been successfully downloaded!");
        }

        return 0;
    }

    private void downloadFromHost(ClientMeta client) {
        String host = Byte.toUnsignedInt(client.ip[0]) + "." + Byte.toUnsignedInt(client.ip[1]) + "." + Byte.toUnsignedInt(client.ip[2]) + "." +
                Byte.toUnsignedInt(client.ip[3]);
        if (host.equals("127.0.0.1") && client.port == context.myServerPort) {
            return;
        }

        System.out.println("downloading ...");

        try {
            Socket socket = new Socket(host, client.port);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            ClientRequestStat requestStat = new ClientRequestStat(fileId);
            requestStat.writeToDataOutputStream(dataOutputStream);
            ClientResponseStat responseStat = new ClientResponseStat(dataInputStream);

            for (int partId : responseStat.parts) {
                if (fileMeta.getBlock(partId) == null) {
                    fileMeta.blocks.computeIfAbsent(partId, (Integer blockId) -> {
                        try {

                            ClientRequestGet requestGet = new ClientRequestGet(fileId, blockId);
                            requestGet.writeToDataOutputStream(dataOutputStream);
                            ClientResponseGet responseGet = new ClientResponseGet(dataInputStream);

                            System.out.println("\tblock " + (blockId + 1) + "/" + numBlocks +
                                    " from " + host + ":" + client.port +
                                    " size=" + responseGet.contentSize);
                            return new BlockMeta(blockId, responseGet.contentSize, responseGet.content);

                        } catch (IOException e) {
                            System.err.println("\tfail download block " + blockId + " from " + host + ":" + client.port);
                            return null;
                        }

                    });
                }
            }
            socket.close();

        } catch (IOException e) {
            System.err.println("Connection to " + host + ":" + client.port + " broken!");
        }

    }
}

