package Seed.Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Catalog {
    final int blockSize;
    final ConcurrentHashMap<Integer, FileMeta> idFileMap;
    final Path serializationPath;

    public Catalog(int blockSize, String catalogURI) throws IOException, ClassNotFoundException {
        this.blockSize = blockSize;
        this.serializationPath = Paths.get(catalogURI);
        if (!Files.exists(serializationPath)) {
            Files.createFile(serializationPath);
            idFileMap = new ConcurrentHashMap<>();
        } else {
            FileInputStream fileIn = new FileInputStream(serializationPath.toFile());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileIn);
            idFileMap = (ConcurrentHashMap<Integer, FileMeta>) objectInputStream.readObject();
        }
    }

//    public FileMeta addNewFile(Path absoluteFilePath) {
//
//        // TODO: load file, split it into blocks and add to idFIleMap
//    }

    public void addFile(FileMeta fileMeta) {
        idFileMap.put(fileMeta.id, fileMeta);
    }

    public FileMeta getFile(int id) {
        return idFileMap.get(id);
    }

    public void removeFile(int id) {
        idFileMap.remove(id);
    }

    public List<FileMeta> getFiles() {
        ArrayList<FileMeta> files;
        synchronized (idFileMap) {
            files = new ArrayList<>(idFileMap.values());
        }
        return files;
    }

    public void storeCatalog() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(serializationPath.toFile());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOut);
        objectOutputStream.writeObject(idFileMap);
        objectOutputStream.flush();
    }

}
