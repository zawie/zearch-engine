package zearch.query;

import zearch.gram.GramData;
import zearch.gram.Grammifier;
import zearch.index.IndexDatabase;
import zearch.index.URLScorePair;
import zearch.spider.Crawler;

import java.sql.SQLException;
import java.util.AbstractQueue;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class SearchEngine {

    public static void main(String[] args) throws Exception {

        String dbFilepath = args[0];
        IndexDatabase.connect(dbFilepath);

        String query = args[1];

        System.out.println("query: "+query);

        Integer amount = args.length > 2 ? Integer.parseInt(args[2]) : 16;
        List<URLScorePair> results = search(query, amount);
        for (URLScorePair r : results) {
            System.out.println(r);
        }
    }
        public static List<URLScorePair> search(String query) throws SQLException {
            return search(query, 16);
        }
        public static List<URLScorePair> search(String query, Integer amount) throws SQLException {
        Map<String, Integer> grams = Grammifier.grammify(query);
        grams.keySet().retainAll(GramData.SINGLETON.getGrams());

        int totalCount = 0;
        for (Integer count : grams.values())
            totalCount += count;

        List<URLScorePair> results = IndexDatabase.best(grams, amount);
        for (URLScorePair res : results) {
            res.setScore(res.getScore()/totalCount);
        }
        return results;
    }
}
