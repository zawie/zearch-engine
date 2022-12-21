package zearch.gram;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Grammifier {

    public static Map<String,Short> grammify(String str) {
        return count3Grams(alphanumeritize(str));
    }
    private static String alphanumeritize(String str) {
        return str.replaceAll("\s+", " ")
                  .replaceAll("[^a-zA-Z0-9\\s]", "")
                  .toLowerCase();
    }
    private static Map<String, Short> count3Grams(String str) {
        Map<String, Short> gramToCount = new HashMap<>();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            String gram = ""+chars[i]+chars[i+1]+chars[i+2];
            gramToCount.put(gram, (short) (gramToCount.getOrDefault(gram, (short) 0) + 1));
        }
        return gramToCount;
    }

    public static Iterator<String> grams() {
        return new Iterator<>() {

            char[] intToChar = "abcdefghijklmnopqrstuvwxyz 0123456789".toCharArray();
            int i = 0;
            int j = 0;
            int k = 0;
            @Override
            public boolean hasNext() {
                return k < intToChar.length;
            }

            @Override
            public String next() {
                String gram = ""+intToChar[i]+intToChar[j]+intToChar[k];
                i++;
                if (i == intToChar.length) {
                    i = 0;
                    j++;
                    if (j == intToChar.length) {
                        j = 0;
                        k++;
                    }
                }
                return gram;
            }
        };
    }
}
