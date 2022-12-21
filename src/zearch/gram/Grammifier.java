package zearch.gram;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Grammifier {

    public static Map<String,Integer> grammify(String str) {
        return count3Grams(alphanumeritize(str));
    }

    private static Collection<String> indexGrams = GramData.SINGLETON.getGrams();


    /*
        Score("abc") = log(num tries in doc) + log(abc's in english) - log(abc's in doc)
        Note: Smaller score is "better".
     */
    public static Map<String, Integer> computeGramScore(String str) {
        Map<String, Integer> gramToCount = grammify(str);

        Map<String, Double> gramToScoreDouble = new HashMap<>();
        int totalCount = 0;
        for (String gram : gramToCount.keySet()) {
            if (!indexGrams.contains(gram))
                continue;
            Integer count = gramToCount.get(gram);
            if (count <= 0)
                continue;
            totalCount += count;

            gramToScoreDouble.put(gram, Math.log(GramData.SINGLETON.getCount(gram)) - Math.log(count));
        }

        Map<String, Integer> gramToScore= new HashMap<>();
        for (String gram : gramToCount.keySet()) {
            if (!indexGrams.contains(gram))
                continue;
            gramToScore.put(gram, (int) ((gramToScoreDouble.get(gram) + Math.log(totalCount))/Math.log(2)));
        }

        return gramToScore;
    }

    private static String alphanumeritize(String str) {
        return str.replaceAll("\s+", " ")
                  .replaceAll("[^a-zA-Z0-9\\s]", "")
                  .toLowerCase();
    }
    private static Map<String, Integer> count3Grams(String str) {
        Map<String, Integer> gramToCount = new HashMap<>();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            String gram = ""+chars[i]+chars[i+1]+chars[i+2];
            gramToCount.put(gram, gramToCount.getOrDefault(gram, 0) + 1);
        }
        return gramToCount;
    }


}
