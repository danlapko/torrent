package Tracker.Model;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FileMeta {
    public final int id;
    public final String name;
    public final long size;
    private final Set<ClientMeta> clients = new HashSet<>();


    public FileMeta(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public void addClient(ClientMeta client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void removeClient(ClientMeta client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public Set<ClientMeta> getClients() {
        Set<ClientMeta> clients_;
        synchronized (clients) {
            clients_ = new HashSet<>(clients);
        }
        return clients_;
    }

    public void removeMe() {
        synchronized (clients) {
            for (ClientMeta client : clients) {
                client.removeMe();
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FileMeta)) {
            return false;
        }

        FileMeta that = (FileMeta) other;

        return id == that.id && name.equals(that.name) && size == that.size;
    }

    @Override
    public int hashCode() {
        return (String.valueOf(id) + name + String.valueOf(size)).hashCode();
    }
}
