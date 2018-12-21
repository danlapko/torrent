package Seed.Model;

import java.io.Serializable;

public class BlockMeta implements Serializable {
    final int id;
    public final int size;
    public final byte[] data;

    public BlockMeta(int id, int size, byte[] data) {
        this.id = id;
        this.size = size;
        this.data = data;
    }
}
