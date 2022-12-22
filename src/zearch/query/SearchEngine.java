package zearch.query;

import zearch.gram.GramData;
import zearch.gram.Grammifier;
import zearch.index.IndexDatabase;
import zearch.index.URLScorePair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SearchEngine {

    public List<URLScorePair> search(String query) throws SQLException {
        Map<String, Integer> grams = Grammifier.grammify(query);
        grams.keySet().retainAll(GramData.SINGLETON.getGrams());

        int totalCount = 0;
        for (Integer count : grams.values())
            totalCount += count;

        List<URLScorePair> results = IndexDatabase.best(grams);
        for (URLScorePair res : results) {
            res.setScore(res.getScore()/totalCount);
        }

        return results;
    }
}
