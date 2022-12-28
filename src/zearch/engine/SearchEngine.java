package zearch.engine;

import zearch.database.IndexEntry;
import zearch.minhash.MinHasher;

import java.util.*;

public class SearchEngine {

    private ISearchEngineToModel model;

    private MinhashTable<Long> minhashTable;
    public SearchEngine(ISearchEngineToModel model) {
        this.model = model;
        generateMinhashTable();
    }

    public SearchResult search(String query) {
        int hashes[] = model.computeMinhashes(query);
        Collection<Long> rowIds = minhashTable.query(hashes);

        System.out.println("Search results:");
        for (Long id : rowIds) {
            Map<String, String> metaData = model.getMetaData(id);
            System.out.println(" - " + metaData.getOrDefault("title", "No Title")+ ": " + metaData.getOrDefault("url", "No URL"));
        }
        System.out.println("End search results:");

        return new SearchResult();
    }

    private void generateMinhashTable() {
        int numEntries = model.getNumberOfIndexEntries();

        System.out.println("Num entires: " + numEntries);
        int K = Math.max((int) (Math.log(numEntries) / Math.log(256)), 1); // log base 256
        int L = MinHasher.COUNT/K;

        this.minhashTable = new MinhashTable<>(K, L);

        Iterator<IndexEntry> iter = model.getAllIndexEntries();
        while (iter.hasNext()) {
            IndexEntry entry = iter.next();
            System.out.println(entry.getID() + ": " + entry.getHashes()[0] + ", " + entry.getHashes()[1]);
            minhashTable.insert(entry.getID(), entry.getHashes());
        }
    }

}
