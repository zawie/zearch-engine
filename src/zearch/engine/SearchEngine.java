package zearch.engine;

import zearch.util.IndexHashesEntry;
import zearch.engine.datastructures.MinhashTable;
import zearch.engine.similarity.ComboSimilarity;
import zearch.engine.similarity.ISimilarity;
import zearch.engine.similarity.word.JaccardWordSimilarity;
import zearch.minhash.MinHasher;
import zearch.engine.similarity.gram.GramSimilarity;
import zearch.util.Pair;
import zearch.util.TopKCollector;

import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {

    private ISearchEngineToModel model;
    private static final int MAX_INDEPTH_COMPARED = 5000;
    private static final int MAX_RETURNED = 30;


    private ISimilarity similarity = new ComboSimilarity(
            new GramSimilarity(),
            new JaccardWordSimilarity()
    );

    private MinhashTable<Long> minhashTable;
    public SearchEngine(ISearchEngineToModel model) {
        this.model = model;
        generateMinhashTable();
    }

    public SearchResult search(String query) {
        System.out.println("Query: '"+query+"'");
        int hashes[] = model.computeMinhashes(query);

        long t0 = System.nanoTime();
        var results =  minhashTable.query(hashes)
                .unordered()
                .limit(MAX_INDEPTH_COMPARED)
                .parallel()
                .map(id -> model.getData(id))
                .map(data -> {
                    double score = similarity.similarity(
                            query,
                            data.values().stream().parallel().collect(Collectors.joining(" "))
                    );
                    if (!data.containsKey("description"))
                        score /= 10;
                    if (!data.containsKey("title"))
                        score /= 20;
                    return new Pair<>(data, score);
                })
                .collect(new TopKCollector<>(MAX_RETURNED, Comparator.comparingDouble(Pair::getSecond)));
        long t1 = System.nanoTime();

        return new SearchResult(query, results, ( (double) (t1 - t0)) / 1000000000.0);
    }

    private void generateMinhashTable() {
        int numEntries = model.getNumberOfIndexEntries();

        System.out.println("Num entries: " + numEntries);
        int K = Math.max((int) (Math.log(numEntries) / Math.log(256)), 1); // log base 256
        int L = MinHasher.COUNT/K;

        this.minhashTable = new MinhashTable<>(K, L);

        Iterator<IndexHashesEntry> iter = model.getAllIndexEntries();
        while (iter.hasNext()) {
            IndexHashesEntry entry = iter.next();
            minhashTable.insert(entry.getID(), entry.getHashes());
        }
    }

    public void refresh() {
        generateMinhashTable();
    }

}
