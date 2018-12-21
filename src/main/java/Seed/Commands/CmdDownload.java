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
            throw new NotAvaliableFile(String.valueOf(fileId));
        }


        List<ClientMeta> sources = context.getFileSources(fileId);

        for (ClientMeta client : sources) {
            String host = client.ip[0] + "." + client.ip[1] + "." + client.ip[2] + "." + client.ip[3];
            if (host.equals("127.0.0.1") && client.port == context.myServerPort) {
                continue;
            }
            System.out.println("downloading from " + host + ":" + client.port + " ...");
            Socket socket = new Socket(host, client.port);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            ClientRequestStat requestStat = new ClientRequestStat(fileId);
            requestStat.writeToDataOutputStream(dataOutputStream);
            ClientResponseStat responseStat = new ClientResponseStat(dataInputStream);

            long numBlocks = (fileMeta.size + context.blockSize - 1) / context.blockSize;

            for (int partId : responseStat.parts) {
                if (fileMeta.getBlock(partId) == null) {
                    System.out.println("\tblock " + partId + "/" + numBlocks);

                    ClientRequestGet requestGet = new ClientRequestGet(fileId, partId);
                    requestGet.writeToDataOutputStream(dataOutputStream);
                    ClientResponseGet responseGet = new ClientResponseGet(dataInputStream);
                    fileMeta.addBlock(new BlockMeta(partId, responseGet.contentSize, responseGet.content));
                }
            }
        }

        System.out.println("File " + fileMeta.name + " whith id=" + fileMeta.id + " have been downloaded!");


        return 0;
    }
}

