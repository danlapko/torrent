package Tracker.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Catalog {

    private final AtomicInteger maxId = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, FileMeta> filesMap = new ConcurrentHashMap<>();


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


}
