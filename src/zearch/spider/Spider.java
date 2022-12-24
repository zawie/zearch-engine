package zearch.spider;

import zearch.index.IndexDatabase;
import zearch.spider.robots.RobotsParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class Spider {
    public static void main(String[] args) throws Exception {

        String dbFilepath = args[0];
        Integer numCrawlers = Integer.parseInt(args[1]);

        IndexDatabase.connect(dbFilepath);

        List<String> urls = new LinkedList<>();
        for (int i = 2; i < args.length; i++) {
            urls.add(args[i]);
        }

        run(numCrawlers, urls);
    }

    public static void run(int numCrawlers, List<String> startUrls) throws MalformedURLException {
        URLPool urlPool = new URLPool();

        System.out.println("Enqueuing initial " + startUrls.size() + " links.");
        for (String link : startUrls) {
            if (!link.startsWith("https://") && !link.startsWith("http://")) {
                link = "https://" + link;
            }
            urlPool.push(new URL(link));
        }

        for (int i = 0; i < numCrawlers; i++) {
            new Thread(new Crawler(urlPool, 10)).start();
        }
    }
}
