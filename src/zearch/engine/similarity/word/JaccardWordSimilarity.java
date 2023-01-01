package zearch.engine.similarity.word;

import zearch.engine.similarity.ISimilarity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardWordSimilarity implements ISimilarity {

    @Override
    public double similarity(String a, String b) {
        String query = alphanumeritize(a);
        String document = alphanumeritize(b);

        Set<String> queryWords = new HashSet<>(List.of(query.split(" ")));
        Set<String> documentWords = new HashSet<>(List.of(document.split(" ")));

        Set<String> numerator = new HashSet<>();
        Set<String> denominator = new HashSet<>();

        numerator.addAll(queryWords);
        numerator.retainAll(documentWords);

        denominator.addAll(queryWords);
        denominator.addAll(documentWords);

        // Jaccard similarity of words
        return ((double) numerator.size() )/ ((double) denominator.size());
    }

    private static String alphanumeritize(String str) {
        return str.replaceAll("(\\s|\n)+", " ")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .toLowerCase();
    }
}
