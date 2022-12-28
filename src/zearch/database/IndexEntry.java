package zearch.database;

import java.util.Arrays;

public class IndexEntry {

    private long id;
    private int[] hashes;
    public IndexEntry(long id, int[] hashes) {
        this.id = id;
        this.hashes = Arrays.copyOf(hashes, hashes.length);
    }

    public long getID() {
        return id;
    }

    public int[] getHashes() {
        return hashes;
    }
}
