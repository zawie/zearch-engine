package zearch.engine.similarity.gram;
import zearch.engine.similarity.ISimilarity;

import java.util.Map;

public class GramSimilarity implements ISimilarity {

    /**
     * Note: Non-commutative! String "a" is the primary key.
     * @param a the key to measure similarity to
     * @param b a candidate string
     * @return an arbitrary score: the higher, the more similar)
     */
    public double similarity(String a, String b) {

        double score = 0;

        Map<String, Integer> aGrams = Grammifier.grammify(a);
        Map<String, Integer> bGrams = Grammifier.grammify(b);

        long aGramCount = 0;
        for (String gram : aGrams.keySet()) {
            aGramCount += aGrams.get(gram);
        }

        long bGramCount = 0;
        for (String gram : bGrams.keySet()) {
            bGramCount += bGrams.get(gram);
        }

        for (String gram : aGrams.keySet()) {
            score += (
                (double) aGrams.get(gram) * bGrams.getOrDefault(gram,0)
            )/(
                (double) aGramCount * bGramCount * GramData.SINGLETON.getCount(gram)
            ) ;
        }

        return score;
    }

 }
