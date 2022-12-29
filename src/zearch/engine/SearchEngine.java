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

//        System.out.println("Number of results: " + rowIds.size());
//        System.out.println("\\/\\/\\/Search results:\\/\\/\\/");
        for (Long id : rowIds) {
            Map<String, String> data = model.getData(id);

//           System.out.println(data.getOrDefault("title", "No Title")+ ":\t" + data.getOrDefault("url", "No URL"));
        }
//        System.out.println("/\\/\\/\\End search results/\\/\\/\\");
        return new SearchResult();
    }

    private void generateMinhashTable() {
        int numEntries = model.getNumberOfIndexEntries();

        System.out.println("Num entires: " + numEntries);
        int K = Math.max((int) (Math.log(numEntries) / Math.log(256)), 1); // log base 256
        int L = 1; //MinHasher.COUNT/K;

        this.minhashTable = new MinhashTable<>(K, L);

        Iterator<IndexEntry> iter = model.getAllIndexEntries();
        while (iter.hasNext()) {
            IndexEntry entry = iter.next();
            minhashTable.insert(entry.getID(), entry.getHashes());
        }
    }

}
