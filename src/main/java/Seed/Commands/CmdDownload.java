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
import java.util.List;

@io.airlift.airline.Command(name = "download", description = "download file from torrent by its id")
public class CmdDownload implements Command {

    @Arguments(description = "file id obtained from tracker", required = true)
    private int fileId;

    @Override
    public int execute(GlobalContext context) throws Exception {
        List<FileMeta> files = context.getTrackerList();
        FileMeta fileMeta = null;
        for (FileMeta file : files) {
            if (file.id == fileId) {
                fileMeta = file;
            }
        }

        if (fileMeta == null) {
            throw new NotAvaliableFile("Tracker has not file with such id: " + fileId + "!");
        }

        long numBlocks = (fileMeta.size + context.blockSize - 1) / context.blockSize;

        List<ClientMeta> sources = context.getFileSources(fileId);

        for (ClientMeta client : sources) {
            String host = Byte.toUnsignedInt(client.ip[0]) + "." + Byte.toUnsignedInt(client.ip[1]) + "." + Byte.toUnsignedInt(client.ip[2]) + "." +
                    Byte.toUnsignedInt(client.ip[3]);

            if (host.equals("127.0.0.1") && client.port == context.myServerPort) {
                continue;
            }

            System.out.println("downloading from " + host + ":" + client.port + " ...");

            try {
                Socket socket = new Socket(host, client.port);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                ClientRequestStat requestStat = new ClientRequestStat(fileId);
                requestStat.writeToDataOutputStream(dataOutputStream);
                ClientResponseStat responseStat = new ClientResponseStat(dataInputStream);

                for (int partId : responseStat.parts) {
                    if (fileMeta.getBlock(partId) == null) {
                        System.out.print("\tblock " + (partId + 1) + "/" + numBlocks);

                        ClientRequestGet requestGet = new ClientRequestGet(fileId, partId);
                        requestGet.writeToDataOutputStream(dataOutputStream);
                        ClientResponseGet responseGet = new ClientResponseGet(dataInputStream);
                        System.out.println(" size=" + responseGet.contentSize);
                        fileMeta.addBlock(new BlockMeta(partId, responseGet.contentSize, responseGet.content));
                    }
                }

            } catch (IOException e) {
                System.err.println("Connection to " + host + ":" + client.port + " broken!");
            }
        }
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
}

