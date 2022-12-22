package zearch.spider;

import zearch.index.IndexDatabase;

import java.net.URL;
import java.sql.SQLException;
import java.util.AbstractQueue;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class Spider {
    public static void main(String[] args) throws Exception {

        if (args[0] == "help") {
            System.out.println("Spider parameters: <database filepath> <num crawlers> [starting domains]");
        }

        String dbFilepath = args[0];
        Integer numCrawlers = Integer.parseInt(args[1]);

        IndexDatabase.connect(dbFilepath);

        Deque<URL> urlDeque = new LinkedBlockingDeque<>( 1024);
        for (int i = 2; i < args.length; i++) {
            urlDeque.push(new URL(args[i]));
        }

        for (int i = 0; i < numCrawlers; i++) {
            Crawler crawler = new Crawler(urlDeque, 1000);
            crawler.start();
        }
    }
}
