package zearch.spider;

import zearch.spider.util.RoundPartition;
import zearch.spider.robots.RobotsCache;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class URLPool implements IPool<URL> {

    private RoundPartition<String, URL> roundPartition;
    private Set<URL> visited;
    private int maxVisitedSize = 1048*2048;
    private RobotsCache robots;
    private Map<String, Long> ungroundTime;
    void URLPool() {
        // RoundPartition<Host, URL>
        roundPartition = new RoundPartition<>(1048, 1048);
        visited = new HashSet<>();
        robots = new RobotsCache(2048);
        ungroundTime = new HashMap<>();
    }
    @Override
    public void push(URL url) {
        String host = url.getHost();
        if (visited.contains(url))
            return;
        if (!robots.isAllowed(url))
            return;

        boolean addSuccess = roundPartition.addTo(host, url);
        if (!addSuccess)
            return;

        if (visited.size() > maxVisitedSize)
            visited.clear();
        visited.add(url);
    }

    @Override
    public URL pull() {
        return attemptPull(16);
    }

    public URL attemptPull(int retryCount) {
        long t = System.currentTimeMillis();

        URL url = roundPartition.pullNextPartition();
        String host = url.getHost();
        Long ripeTime = ungroundTime.get(host);
        if (ripeTime != null && t < ripeTime) {
            roundPartition.addTo(host, url);
            if (retryCount > 0)
                return attemptPull(retryCount - 1);
            return null;
        }
        groundHost(url, 100);
        return url;
    }

    public void groundHost(URL url, int timeInMillis) {
        ungroundTime.put(url.getHost(), timeInMillis +  System.currentTimeMillis());
    }
}
