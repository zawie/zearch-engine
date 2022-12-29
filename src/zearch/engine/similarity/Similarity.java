package zearch.engine.similarity;
import java.util.HashMap;
import java.util.Map;

public class Similarity {

    /**
     * Note: Non-commutative! String "a" is the primary key.
     * @param a the key to measure similarity to
     * @param b a candidate string
     * @return an arbitrary score: the higher, the more similar)
     */
    public static
    double similarity(String a, String b) {

        double score = 0;

        Map<String, Integer> aGrams =Grammifier.grammify(a);
        Map<String, Integer> bGrams =Grammifier.grammify(b);

        int aGramCount = 0;
        for (String gram : aGrams.keySet()) {
            aGramCount += aGrams.get(gram);
            score += ((double) aGrams.get(gram)*bGrams.getOrDefault(gram,0))/((double)GramData.SINGLETON.getCount(gram));
        }
        int bGramCount = 0;
        for (String gram : bGrams.keySet()) {
            bGramCount += bGrams.get(gram);
        }

        score *= ((double) GramData.SINGLETON.getNumTris())/((double) (aGramCount*bGramCount));
        return score;
    }

 }
