package zearch.engine.similarity.gram;

import java.util.HashMap;
import java.util.Map;

public class Grammifier {

    public static Map<String, Integer> grammify(String str) {
        return count3Grams(alphanumeritize(str));
    }
    private static String alphanumeritize(String str) {
        return str.replaceAll("(\\s|\n)+", " ")
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