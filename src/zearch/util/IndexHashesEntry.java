package zearch.util;

import java.util.Arrays;

public class IndexHashesEntry {

    private long id;
    private int[] hashes;
    public IndexHashesEntry(long id, int[] hashes) {
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
