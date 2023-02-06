package zearch.engine.datastructures;

import java.util.*;
import java.util.stream.Stream;

public class MinhashTable<E> {

    private LinkedHashSet<E>[] table;
    private final int K;
    private final int L;

    private final int B = 100000;
    public MinhashTable(int K, int L) {
        this.L = L;
        this.K = K;

        this.table = new LinkedHashSet[B];
    }

    public void insert(E element, int hashes[]) {
        assert(hashes.length >= K*L);
        int[] keys = new int[K + 1];
        for (int l = 0; l < L; l++) {
            keys[K] = l;
           for (int k = 0; k < K; k++)
               keys[k] = hashes[l*K + k];
            Integer key = Arrays.hashCode(keys);
            if (table[key % B] == null) {
                table[key % B] = new LinkedHashSet<>();
            }
            table[key % B].add(element);
        }
    }

    public Stream<E> query(int[] hashes) {
        assert(hashes.length >= K*L);
        return Stream.iterate(0, n-> n+1)
            .limit(L)
            .parallel()
            .map(l -> {
                int[] keys = new int[K + 1];
                keys[K] = l;
                for (int k = 0; k < K; k++)
                    keys[k] = hashes[l*K + k];
                return keys;
            })
            .map(Arrays::hashCode)
            .filter(key -> table[key % B] != null)
            .flatMap(key -> table[key % B].stream());
    }
}
