package zearch.spider;

import org.jsoup.nodes.Document;
import zearch.index.IndexDatabase;
import zearch.query.SearchEngine;
import zearch.spider.scraper.Scraper;

import java.net.URL;
import java.util.*;

public class Crawler implements Runnable {

    private URLPool urlPool;
    private int period;

    public Crawler(URLPool urlPool, Integer period) {
        this.urlPool = urlPool;
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
            } catch (Exception e) {
                System.out.println(
                        "Crawler " + id + " encountered an exception: " + e.toString() + e.getMessage());
//                e.printStackTrace();
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
        URL url = null;
        try {
            url = urlPool.pull();
        } catch (NoSuchElementException e) {
            System.out.println(
                    "Crawler " + id +" couldn't find valid element. Waiting");
            sleep(1000);
            return;
        }

        System.out.println(url);
        Document doc = null;
        try {
           doc = Scraper.getDocumentFromURL(url);
        } catch (Exception e) {
            System.out.println("Exception "+e.toString()+" on "+url+": "+e.getMessage());
            urlPool.groundHost(url, 60*1000); // Ground bad host for 1 minute.
        }
        Scraper.parseLinks(url, doc, urlPool);
        Map<String, Integer> score = Scraper.computeDocumentScore(url.toString(), doc);
        IndexDatabase.write(url.toExternalForm(), score);
    }
}