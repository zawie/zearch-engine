package zearch.spider;

import zearch.index.IndexDatabase;
import zearch.spider.robots.RobotsParser;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class Spider {
    public static void main(String[] args) throws Exception {

        if (args[0] == "help") {
            System.out.println("Spider parameters: <database filepath> <num crawlers> [starting domains]");
        }

        String dbFilepath = args[0];
        Integer numCrawlers = Integer.parseInt(args[1]);

        IndexDatabase.connect(dbFilepath);

        int capacity = 2048;
        Deque<URL> urlDeque = new LinkedBlockingDeque<>( capacity);
        Set<URL> urlVisited = new HashSet<>();

        IPool<URL> urlPool = new IPool<>() {
            boolean stackQueueToggle = false;
            @Override
            public void push(URL url) {
                if (urlVisited.contains(url))
                    return;
                if (!RobotsParser.SINGLETON.isAllowed(url)) {
                    System.out.println(url.toString() + " is disallowed");
                    return;
                }
                try {
                    urlDeque.addLast(url);
                } catch (IllegalStateException e) {
                    return;
                }
                urlVisited.add(url);
                if (urlVisited.size() > 2048) {
                    urlVisited.clear();
                }
            }

            @Override
            public URL pull() {
                stackQueueToggle = !stackQueueToggle;
                if (stackQueueToggle)
                    return urlDeque.removeFirst();
                return urlDeque.removeLast();
            }
        };

        System.out.println("Enqueuing initial " + (args.length - 2) + " links.");
        for (int i = 2; i < args.length; i++) {
            String link = args[i];
            if (!link.startsWith("https://") && !link.startsWith("http://")) {
                link = "https://" + link;
            }
            urlPool.push(new URL(link));
        }

        for (int i = 0; i < numCrawlers; i++) {
            Crawler crawler = new Crawler(urlPool, 100);
            crawler.start();
        }
    }
}
