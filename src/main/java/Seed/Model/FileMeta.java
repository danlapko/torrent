package Seed.Model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FileMeta implements Serializable {
    public final int id;
    public final String name;
    public final long size;
    private final ConcurrentHashMap<Integer, BlockMeta> blocks = new ConcurrentHashMap<>();


    public FileMeta(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public void addBlock(BlockMeta block) {
        blocks.put(block.id, block);
    }

    public void removeBlock(int id) {
        blocks.remove(id);
    }

    public BlockMeta getBlock(int id) {
        return blocks.get(id);
    }

    public int getNumBlocks() {
        return blocks.size();
    }

    public int[] getBlockIds() {
        int ids[] = blocks.keySet().stream().mapToInt(x -> x).toArray();
        return ids;
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

    public boolean containsBlock(int blockId) {
        return blocks.containsKey(id);

    }
}
