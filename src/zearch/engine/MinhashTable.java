package zearch.engine;

import java.util.*;

public class MinhashTable<E> {

    private List<Map<Integer, Set<E>>> hashtables;
    private int K;
    private int L;
    public MinhashTable(int K, int L) {
        this.L = L;
        this.K = K;
        this.hashtables = new ArrayList<>();
        for (int l = 0; l < L; l++) {
            hashtables.add(new HashMap<>());
        }
    }

    public void insert(E element, int hashes[]) {
        assert(hashes.length >= K*L);
        for (int l = 0; l < L; l++) {
            Map<Integer, Set<E>> table = hashtables.get(l);
           int[] keys = new int[K];
           for (int k = 0; k < K; k++)
               keys[k] = hashes[l*K + k];
           int key = keys.hashCode();
           if (!table.containsKey(key))
               table.put(key ,new HashSet<>());
           table.get(key).add(element);
        }
    }

    public Collection<E> query(int hashes[]) {
        assert(hashes.length >= K*L);
        Set<E> output = new HashSet<>();
        for (int l = 0; l < L; l++) {
            Map<Integer, Set<E>> table = hashtables.get(l);
            int[] keys = new int[K];
            for (int k = 0; k < K; k++)
                keys[k] = hashes[l*K + k];
            int key = keys.hashCode();
            output.addAll(table.getOrDefault(key, Collections.emptySet()));
        }
        return output;
    }
}
