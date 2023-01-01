package zearch.engine.similarity;

import org.junit.jupiter.api.Test;
import zearch.engine.similarity.gram.Grammifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GrammifierTest {

    @Test
    void testThe() {
        Map<String,Integer> grams = Grammifier.grammify("the");
        assertEquals(0, grams.get("the"), 1);
    }

    @Test
    void testAbcs() {
        Map<String,Integer> grams = Grammifier.grammify("abcdef abc");
        assertEquals(0, grams.get("abc"), 2);
        assertEquals(0, grams.get("bcd"), 1);
        assertEquals(null, grams.get("efa"));

    }

}