package zearch.spider;

import java.util.AbstractQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Spider {
    public static void main(String[] args) {

        if (args[0] == "help") {
            System.out.println("Spider parameters: <database filepath> <num crawlers> <min period in ms>");
        }

        String dbFilepath = args[0];
        Integer numCrawlers = Integer.parseInt(args[1]);
        Integer period = Integer.parseInt(args[2]);

        AbstractQueue<String> urlQueue = new LinkedBlockingDeque<>( 1024);

        for (int i = 0; i < numCrawlers; i++) {
            Crawler crawler = new Crawler(urlQueue, period);
            crawler.start();
        }
    }
}
