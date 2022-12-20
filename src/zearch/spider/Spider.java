package zearch.spider;

import java.util.AbstractQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Spider {
    public static void main(String[] args) {
        int numCrawlers = 8;

        AbstractQueue<String> urlQueue = new LinkedBlockingDeque<>(131072);

        for (int i = 0; i < numCrawlers; i++) {
            Crawler crawler = new Crawler(urlQueue);
            crawler.start();
        }
    }
}
