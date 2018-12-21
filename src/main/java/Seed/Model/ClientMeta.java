package Seed.Model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientMeta implements Serializable {
    public final byte[] ip;
    public final short port;

    public ClientMeta(byte[] ip, short port) {
        this.ip = ip;
        this.port = port;
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
