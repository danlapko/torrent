package Tracker.Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Catalog implements Serializable {

    private final AtomicInteger maxId = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, FileMeta> filesMap;
    private final Path serializationPath;

    public Catalog(String catalogURI) throws IOException, ClassNotFoundException {
        serializationPath = Paths.get(catalogURI);
        if (!Files.exists(serializationPath)) {
            Files.createFile(serializationPath);
            filesMap =  new ConcurrentHashMap<>();
        } else {
            FileInputStream fileIn = new FileInputStream(serializationPath.toFile());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileIn);
            filesMap = (ConcurrentHashMap<Integer, FileMeta>) objectInputStream.readObject();
            fileIn.close();
        }
    }

    public FileMeta createAndAdd(String name, long size) {
        int id = maxId.getAndIncrement();
        FileMeta fileMeta = new FileMeta(id, name, size);
        filesMap.put(id, fileMeta);
        return fileMeta;
    }

    public FileMeta getFile(int id) {
        return filesMap.get(id);
    }

    public List<FileMeta> getFiles() {
        ArrayList<FileMeta> files_;
        synchronized (filesMap) {
            files_ = new ArrayList<>(filesMap.values());
        }
        return files_;
    }

    public void storeCatalog() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(serializationPath.toFile());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOut);
        objectOutputStream.writeObject(filesMap);
        objectOutputStream.flush();
    }
}
