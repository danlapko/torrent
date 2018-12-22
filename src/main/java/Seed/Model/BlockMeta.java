package Seed.Model;

import java.io.Serializable;

public class BlockMeta implements Serializable {
    final int id;
    public final long size;
    public final byte[] data;

    public BlockMeta(int id, long size, byte[] data) {
        this.id = id;
        this.size = size;
        this.data = data;
    }
}
