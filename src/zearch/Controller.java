package zearch;

import zearch.database.IndexDatabase;
import zearch.minhash.MinHasher;
import zearch.spider.Spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Controller {

    public static void main(String[] args) {
        String dbFilepath = args[0];
        Integer numCrawlers = Integer.parseInt(args[1]);
        boolean runServer = Boolean.parseBoolean(args[2]);

        try {
            IndexDatabase.connect(dbFilepath);
        } catch (Exception e) {
            System.out.println("Failed to connect to the index database.");
            e.printStackTrace();
            return;
        }

        MinHasher hasher = new MinHasher();
        if (runServer) {
            // TODO: Implement server
        }

        if (numCrawlers > 0) {
            Spider spider = new Spider((url, metaData, textReader) -> {
                try {
                    IndexDatabase.write(url, metaData, hasher.computeHashes(textReader));
                } catch (Exception e) {
                    System.out.println("Encountered an exception while indexing: " +e.getMessage());
                    e.printStackTrace();
                    return;
                }
                System.out.println(" ✓\t" + url);
            });

            // Add initial sites
            for (int i = 3; i < args.length; i++) {
                try {
                    spider.offerURL(new URL(args[i]));
                } catch (MalformedURLException e) {
                    System.out.println(args[i]+ " is not a valid URL.");
                }
            }

            spider.startCrawling(numCrawlers);
        }
    }



}
