package zearch.engine.similarity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimilarityTest {

    @Test
    void test1() {
        assertTrue( Similarity.similarity("abcdefghijklmnopqrstuvwxyz","abcdefghijklmnopqrstuvwxyz") > 0);
    }
}