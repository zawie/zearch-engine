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
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;

public class SearchEngine {

    public static void main(String[] args) throws Exception {

        String dbFilepath = args[0];
        IndexDatabase.connect(dbFilepath);

        Integer amount = 16;
        Scanner scanner = new Scanner(System.in);

        String query;
        System.out.println("\n:::::: Zearch ::::::");
        System.out.print(" ⚲ ");
        while(!(query = scanner.nextLine()).equals("exit")) {
            System.out.println("::::::::::::::::::::");
            if (query.isEmpty())
                continue;
            List<URLScorePair> results = search(query, amount);
            for (URLScorePair r : results) {
                System.out.println(" - "+r.getURL());
            }
            System.out.println("::::::::::::::::::::");
            System.out.println("\n:::::: Zearch ::::::");
            System.out.print(" ⚲ ");
        }

        System.out.println("::::::::::::::::::::");
        System.out.println("Shutting down search engine.");
        IndexDatabase.close();
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
