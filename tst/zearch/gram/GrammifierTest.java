package zearch.gram;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrammifierTest {

    @Test
    void grammifyTest() {
        Map<String, Short> grams = Grammifier.grammify("aBcdEf!gh,ab   cde f;");
        assertEquals(grams.get("abc"), (short) 1);
        assertEquals(grams.get("bcd"), (short) 1);
        assertEquals(grams.get("cde"), (short) 2);
        assertEquals(grams.get("def"), (short) 1);
        assertEquals(grams.get("efg"), (short) 1);
        assertEquals(grams.get("fgh"), (short) 1);
        assertEquals(grams.get("gha"), (short) 1);
        assertEquals(grams.get("hab"), (short) 1);
        assertEquals(grams.get("ab "), (short) 1);
        assertEquals(grams.get("b c"), (short) 1);
        assertEquals(grams.get(" cd"), (short) 1);
        assertEquals(grams.get("de "), (short) 1);
        assertEquals(grams.get("e f"), (short) 1);
        assertEquals(grams.keySet().size(), 13);
    }
}