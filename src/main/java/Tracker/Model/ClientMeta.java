package Tracker.Model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientMeta {
    public final byte[] ip;
    public final short port;

    private final Set<FileMeta> files = ConcurrentHashMap.newKeySet();

    public ClientMeta(byte[] ip, short port) {
        this.ip = ip;
        this.port = port;
    }

    public void addFile(FileMeta file) {
        synchronized (files) {
            files.add(file);
        }
    }

    public void removeFile(FileMeta file) {
        synchronized (files) {
            files.remove(file);
        }
    }

    public Set<FileMeta> getFiles() {
        Set<FileMeta> files_;
        synchronized (files) {
            files_ = new HashSet<>(files);
        }
        return files_;
    }

    public void removeMe() {
        synchronized (files) {
            for (FileMeta file : files) {
                file.removeClient(this);
            }
        }
        files.clear();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ClientMeta)) {
            return false;
        }

        ClientMeta that = (ClientMeta) other;

        return Arrays.equals(ip, that.ip) && port == that.port;
    }


    private String repr() {
        return Arrays.toString(ip) + ":" + String.valueOf(port);
    }

    @Override
    public int hashCode() {
        return repr().hashCode();
    }
}
