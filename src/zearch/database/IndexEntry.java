package zearch.database;

public class IndexEntry {

    private long id;
    private int[] hashes;
    public IndexEntry(long id, int[] hashes) {
        this.id = id;
        this.hashes = hashes;
    }

    public long getID() {
        return id;
    }

    public int[] getHashes() {
        return hashes;
    }
}
