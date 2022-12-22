package zearch.spider;

import zearch.index.IndexDatabase;

import java.sql.SQLException;
import java.util.AbstractQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Spider {
    public static void main(String[] args) throws Exception {

        if (args[0] == "help") {
            System.out.println("Spider parameters: <database filepath> <num crawlers> [starting domains]");
        }

        String dbFilepath = args[0];
        Integer numCrawlers = Integer.parseInt(args[1]);

        IndexDatabase.connect(dbFilepath);

        AbstractQueue<String> urlQueue = new LinkedBlockingDeque<>( 1024);
        for (int i = 2; i < args.length; i++) {
            urlQueue.add(args[i]);
        }

        for (int i = 0; i < numCrawlers; i++) {
            Crawler crawler = new Crawler(urlQueue, 1000);
            crawler.start();
        }
    }
}
