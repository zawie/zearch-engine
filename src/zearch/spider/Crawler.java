package zearch.spider;

import org.jsoup.nodes.Document;
import zearch.index.IndexDatabase;
import zearch.query.SearchEngine;
import zearch.spider.scraper.Scraper;

import java.net.URL;
import java.util.*;

public class Crawler extends Thread {

    private Deque<URL> urlDeque;
    private int period;

    public Crawler(Deque<URL> urlQueue, Integer period) {
        this.urlDeque = urlQueue;
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
                        "Crawler " + id + " encountered an exception:");
                e.printStackTrace();
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
        URL url = urlDeque.pop();
        System.out.println("Crawler "+id+ " scraping :\t" +url);
        Document doc = Scraper.getDocumentFromURL(url);
        Scraper.parseLinks(url, doc, urlDeque);
        Map<String, Integer> score = Scraper.computeDocumentScore(doc);
        IndexDatabase.write(url.toExternalForm(), score);
    }
}