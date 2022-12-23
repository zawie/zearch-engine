package zearch.spider.robots;

import zearch.spider.util.Cache;

import java.net.URL;
import java.util.Set;

public class RobotsCache {

    private Cache<String, Set<String>> cache;
    public RobotsCache(int cacheSize) {
        this.cache = new Cache<>(cacheSize);
    }
    public boolean isAllowed(URL url) {
        Set<String> disallowedPaths = cache.get(url.getHost());
        if (disallowedPaths == null) {
            disallowedPaths = RobotsParser.getDisallowedPaths(url);
            cache.put(url.getHost(), disallowedPaths);
        }

        for (String disallow : disallowedPaths) {
            try {
                if ((url.getPath()+"/").matches(disallow+".*")) {
                    return false;
                }
            } catch (Exception e){
                continue;
            }
        }
        return true;
    }
}
