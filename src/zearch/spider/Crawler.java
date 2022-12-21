package zearch.spider;

import org.jsoup.nodes.Document;
import zearch.spider.scraper.Scraper;

import java.util.Queue;
import java.util.Map;
import java.util.NoSuchElementException;

public class Crawler extends Thread {

    private Queue<String> urlQueue;

    public Crawler(Queue<String> urlQueue) {
        this.urlQueue = urlQueue;
    }

    public void run() {
        Long id = Thread.currentThread().getId();
        System.out.println(
            "Crawler " + id + " is running.");
        while (true) {
            try {
                step();
            } catch (NoSuchElementException e) {
                System.out.println(
                        "Crawler " + id + " found no element in queue.");
                try {
                    Thread.sleep(1000);
                } catch (Exception e_) {
                    // Do nothing.
                }
            } catch (Exception e) {
                System.out.println(
                        "Crawler " + id + " encountered an exception.");
                try {
                    Thread.sleep(1000);
                } catch (Exception e_) {
                    // Do nothing.
                }
            }
        }
    }

    private void step() throws NoSuchElementException, Exception {
        String url = urlQueue.remove();
        Document doc = Scraper.getDocumentFromURL(url);
        Scraper.parseLinks(url, doc, urlQueue);
        Map<String, Integer> gramScore = Scraper.parseGramScore(doc);
    }
}