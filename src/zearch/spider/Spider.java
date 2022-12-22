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
                return urlDeque.removeFirst();
            }
        };

        for (int i = 2; i < args.length; i++) {
            URL url = new URL(args[i]);
            urlPool.push(url);
        }

        for (int i = 0; i < numCrawlers; i++) {
            Crawler crawler = new Crawler(urlPool, 500);
            crawler.start();
        }
    }
}
