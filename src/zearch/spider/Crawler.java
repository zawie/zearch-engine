package zearch.spider;

import org.jsoup.nodes.Document;
import zearch.index.IndexDatabase;
import zearch.query.SearchEngine;
import zearch.spider.scraper.Scraper;

import java.util.AbstractQueue;
import java.util.Queue;
import java.util.Map;
import java.util.NoSuchElementException;

public class Crawler extends Thread {

    private Queue<String> urlQueue;
    private int period;

    public Crawler(Queue<String> urlQueue, Integer period) {
        this.urlQueue = urlQueue;
        this.period = period;
    }

    public void run() {
        Long id = Thread.currentThread().getId();
        System.out.println(
            "Crawler " + id + " is running.");
        while (true) {
            sleep(period);
            try {
                step();
            } catch (NoSuchElementException e) {
                System.out.println(
                        "Crawler " + id + " found no element in queue.");
                sleep(1000);
            } catch (Exception e) {
                System.out.println(
                        "Crawler " + id + " encountered an exception: " + e.getStackTrace());
                sleep(1000);
            }
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void step() throws Exception {
        Long id = Thread.currentThread().getId();
        String url = urlQueue.remove();
        Document doc = Scraper.getDocumentFromURL(url);
        Scraper.parseLinks(url, doc, urlQueue);
        Map<String, Integer> score = Scraper.computeDocumentScore(doc);
        IndexDatabase.write(url, score);
        System.out.println("Crawler "+id+ " scraped :\t" +url);
    }
}