package zearch.spider.robots;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K, V> {

    private LinkedHashMap<K,V> cache;

    public Cache(int maxCapacity) {
        this.cache =  new LinkedHashMap<>() {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxCapacity;
            }
        };
    }

    public V get(K key) {
        if (!this.cache.containsKey(key))
            return null; //Cache miss!
        V value = this.cache.remove(key);
        this.cache.put(key, value);
        return value;
    }

    public put(K key, V value) {
        this.cache.put(key, value);
    }
}
