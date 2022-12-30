package zearch.engine;

import zearch.database.IndexEntry;
import zearch.engine.datastructures.MinhashTable;
import zearch.minhash.MinHasher;
import zearch.engine.similarity.Similarity;
import zearch.util.Pair;

import java.util.*;

public class SearchEngine {

    private ISearchEngineToModel model;
    private static final int MAX_INDEPTH_COMPARED = 10000;
    private static final int MAX_RETURNED = 50;

    private MinhashTable<Long> minhashTable;
    public SearchEngine(ISearchEngineToModel model) {
        this.model = model;
        generateMinhashTable();
    }

    public SearchResult search(String query) {
        int hashes[] = model.computeMinhashes(query);
        Collection<Long> rowIds = minhashTable.query(hashes);

        PriorityQueue<Pair<Map<String, String>, Double>> orderedResults = new PriorityQueue<>(
                (o1, o2) -> -Double.compare(o1.getSecond(), o2.getSecond())
        );

        int idsProcessed = 0;
        for (Long id : rowIds) {
            if (++idsProcessed > MAX_INDEPTH_COMPARED)
                break;
            Map<String, String> data = model.getData(id);
            StringBuilder metaText = new StringBuilder();
            for (String v : data.values()) {
                metaText.append(" ").append(v);
            }
            Double score = Similarity.similarity(query, metaText.toString());
            // Penalize missing meta information
            if (!data.containsKey("description"))
                score /= 10;
            if (!data.containsKey("title"))
                score /= 20;
            orderedResults.add(new Pair<>(data, score));
        }

        List<Map<String, String>> sites = new LinkedList<>();
        for (int i = 0; !orderedResults.isEmpty() && i < MAX_RETURNED; i++) {
            Pair<Map<String, String>, Double> pair = orderedResults.poll();
            sites.add(pair.getFirst());
        }
        SearchResult result = new SearchResult(query, sites);

        return result;
    }

    private void generateMinhashTable() {
        int numEntries = model.getNumberOfIndexEntries();

//        System.out.println("Num entries: " + numEntries);
        int K = Math.max((int) (Math.log(numEntries) / Math.log(256)), 1); // log base 256
        int L = MinHasher.COUNT/K;

        this.minhashTable = new MinhashTable<>(K, L);

        Iterator<IndexEntry> iter = model.getAllIndexEntries();
        while (iter.hasNext()) {
            IndexEntry entry = iter.next();
            minhashTable.insert(entry.getID(), entry.getHashes());
        }
    }

    public void refresh() {
        generateMinhashTable();
    }

}
