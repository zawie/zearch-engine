package zearch.gram;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GramDataTest {

    @Test
    void GramDataTest() {
        assertEquals(GramData.SINGLETON.getCount("the"), 77534223);
    }
}