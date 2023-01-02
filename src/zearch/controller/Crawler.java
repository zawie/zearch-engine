package zearch.controller;

import zearch.minhash.MinHasher;
import zearch.spider.ISpiderToModel;
import zearch.spider.Spider;
import zearch.sqs.index.IndexEnqueuer;
import zearch.util.IndexRowEntry;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Crawler {
        public static void main(String[] args) {
            Integer numCrawlers = Integer.parseInt(args[0]);

            MinHasher hasher = new MinHasher();
            Spider spider = new Spider(new ISpiderToModel() {
                @Override
                public void index(URL url, Map<String, String> metaData, String text) {
                    try {
                        System.out.println("Enqueuing "+url);
                        int[] hashes = hasher.computeHashes(text);
                        IndexEnqueuer.SINGLETON.enqueue(new IndexRowEntry(url, hashes, metaData));
                    } catch (Exception e) {
                        System.out.println("Failed to enqueue: "+url);
                        e.printStackTrace();
                    }
                }

            });

            // Add initial sites
            for (int i = 1; i < args.length; i++) {
                try {
                    spider.offerURL(new URL(args[i]));
                } catch (MalformedURLException e) {
                    System.out.println(args[i]+ " is not a valid URL.");
                }
            }

            spider.startCrawling(numCrawlers);

        }
}
