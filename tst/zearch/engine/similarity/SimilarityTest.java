package zearch.engine.similarity;

import org.junit.jupiter.api.Test;
import zearch.engine.similarity.gram.GramSimilarity;

import static org.junit.jupiter.api.Assertions.*;

class SimilarityTest {

    @Test
    void test1() {
        assertTrue( new GramSimilarity().similarity("abcdefghijklmnopqrstuvwxyz","abcdefghijklmnopqrstuvwxyz") > 0);
    }
}