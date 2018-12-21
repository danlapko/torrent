package Seed;

import Seed.Exceptions.ConnectionBrokenException;
import Seed.Messages.Client.*;
import Seed.Model.Catalog;
import Seed.Model.ClientMeta;
import Seed.Model.FileMeta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class GlobalContext {
    final String trackerHost;
    final int trackerPort;
    final Socket trackerSocket;
    final DataOutputStream trackerDataOutputStream;
    final DataInputStream trackerDataInputStream;
    public final long blockSize;
    public final Catalog catalog;

    final short myServerPort;

    public GlobalContext(String trackerHost, int trackerPort, Catalog catalog, short myServerPort, long blockSize) throws IOException {
        this.trackerHost = trackerHost;
        this.trackerPort = trackerPort;
        this.catalog = catalog;
        this.myServerPort = myServerPort;
        this.blockSize = blockSize;
        this.trackerSocket = new Socket(trackerHost, trackerPort);
        trackerDataOutputStream = new DataOutputStream(trackerSocket.getOutputStream());
        trackerDataInputStream = new DataInputStream(trackerSocket.getInputStream());

        updateMyFilesAtTracker();
    }

    public List<FileMeta> getTrackerList() throws ConnectionBrokenException, IOException {
        try {
            ClientRequestList request = new ClientRequestList();
            request.writeToDataOutputStream(trackerDataOutputStream);
            ClientResponseList response = new ClientResponseList(trackerDataInputStream);
            return response.files;
        } catch (EOFException | SocketException e) {
            throw new ConnectionBrokenException(e);
        }
    }

    public List<ClientMeta> getFileSources(int id) throws ConnectionBrokenException, IOException {
        try {

            ClientRequestSources request = new ClientRequestSources(id);
            request.writeToDataOutputStream(trackerDataOutputStream);
            ClientResponseSources response = new ClientResponseSources(trackerDataInputStream);
            return response.clients;
        } catch (EOFException | SocketException e) {
            throw new ConnectionBrokenException(e);
        }
    }

    public synchronized void updateMyFilesAtTracker() throws ConnectionBrokenException, IOException {
        try {
            List<FileMeta> myFiles = catalog.getFiles();
            int myIds[] = myFiles.stream().mapToInt((fileMeta) -> fileMeta.id).toArray();
            ClientRequestUpdate request = new ClientRequestUpdate(myServerPort, myIds.length, myIds);
            request.writeToDataOutputStream(trackerDataOutputStream);
            ClientResponseUpdate response = new ClientResponseUpdate(trackerDataInputStream);
        } catch (EOFException | SocketException e) {
            throw new ConnectionBrokenException(e);
        }
    }

    public FileMeta uploadTracker(String name, long size) throws ConnectionBrokenException, IOException {
        try {
            ClientRequestUpload request = new ClientRequestUpload(name, size);
            request.writeToDataOutputStream(trackerDataOutputStream);
            ClientResponseUpload response = new ClientResponseUpload(trackerDataInputStream);
            FileMeta fileMeta = new FileMeta(response.id, name, size);
            return fileMeta;
        } catch (EOFException | SocketException e) {
            throw new ConnectionBrokenException(e);
        }
    }

    public List<FileMeta> getSeedingFiles() {
        return catalog.getFiles();
    }

    public synchronized void tryReconnect() throws IOException {
        trackerSocket.connect(new InetSocketAddress(trackerHost, trackerPort));
    }

    public void finish() throws IOException {
        catalog.storeCatalog();
        trackerSocket.close();
    }
}
