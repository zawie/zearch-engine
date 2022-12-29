package zearch.engine.datastructures;

import java.util.*;

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

    public Collection<E> query(int hashes[]) {
        assert(hashes.length >= K*L);
        Set<E> output = new HashSet<>();
        int[] keys = new int[K + 1];
        for (int l = 0; l < L; l++) {
            keys[K] = l;
            for (int k = 0; k < K; k++)
                keys[k] = hashes[l*K + k];
            Integer key = Arrays.hashCode(keys);
            output.addAll(table.getOrDefault(key, Collections.emptySet()));
        }
        return output;
    }
}
