package zearch.gram;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrammifierTest {

    @Test
    void grammifyTest() {
        Map<String, Integer> grams = Grammifier.grammify("aBcdEf!gh,ab   cde f;");
        assertEquals(grams.get("abc"), 1);
        assertEquals(grams.get("bcd"), 1);
        assertEquals(grams.get("cde"), 2);
        assertEquals(grams.get("def"), 1);
        assertEquals(grams.get("efg"), 1);
        assertEquals(grams.get("fgh"), 1);
        assertEquals(grams.get("gha"), 1);
        assertEquals(grams.get("hab"), 1);
        assertEquals(grams.get("ab "), 1);
        assertEquals(grams.get("b c"), 1);
        assertEquals(grams.get(" cd"), 1);
        assertEquals(grams.get("de "), 1);
        assertEquals(grams.get("e f"), 1);
        assertEquals(grams.keySet().size(), 13);
    }
}