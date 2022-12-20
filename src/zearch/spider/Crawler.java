package zearch.spider;

import org.jsoup.nodes.Document;
import zearch.spider.scraper.Scraper;

import java.util.AbstractQueue;
import java.util.Map;
import java.util.NoSuchElementException;

public class Crawler extends Thread {

    private AbstractQueue<String> urlQueue;

    public Crawler(AbstractQueue<String> urlQueue) {
        this.urlQueue = urlQueue;
    }

    public void run() {
        Long id = Thread.currentThread().threadId();
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
        Map<String,Integer> textGrams = Scraper.parseTextGrams(doc);
        Map<String,Integer> metaGrams = Scraper.parseMetaGrams(doc);
    }
}