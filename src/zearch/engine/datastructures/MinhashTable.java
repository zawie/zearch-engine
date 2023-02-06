package zearch.engine.datastructures;

import java.util.*;
import java.util.stream.Stream;

public class MinhashTable<E> {

    private Map<Integer, Set<E>> table;
    private int K;
    private int L;
    public MinhashTable(int K, int L) {
        this.L = L;
        this.K = K;
        this.table = new HashMap<>();
    }

    public void insert(E element, int hashes[]) {
        assert(hashes.length >= K*L);
        int[] keys = new int[K + 1];
        for (int l = 0; l < L; l++) {
            keys[K] = l;
           for (int k = 0; k < K; k++)
               keys[k] = hashes[l*K + k];
            Integer key = Arrays.hashCode(keys);
            table.putIfAbsent(key, new HashSet<>());
            table.get(key).add(element);
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
            .filter(key -> table.containsKey(key))
            .flatMap(key -> table.get(key).stream());
    }
}
