package zearch.engine;

import org.junit.jupiter.api.Test;
import zearch.engine.datastructures.MinhashTable;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MinhashTableTest {

    @Test
    void test1() {
        MinhashTable<Integer> table = new MinhashTable<>(2,2);
        int hashes[] = new int[]{0,1,2,3};
        Integer element1 = 9;
        Integer element2 = 8;
        int hashes2[] = new int[]{10,11,12,13};
        Integer element3 = 7;

        table.insert(element1, hashes);
        table.insert(element2, hashes);
        table.insert(element3, hashes2);

        assertEquals(Set.of(element1,element2), table.query(hashes));
        assertEquals(Set.of(element3), table.query(hashes2));
    }
}