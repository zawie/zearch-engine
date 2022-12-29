package zearch;

import zearch.database.IndexDatabase;
import zearch.database.IndexEntry;
import zearch.engine.ISearchEngineToModel;
import zearch.engine.SearchEngine;
import zearch.engine.SearchResult;
import zearch.minhash.MinHasher;
import zearch.spider.Spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

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
        SearchEngine searchEngine = null;
        if (runServer || numCrawlers <= 0) {
            searchEngine = new SearchEngine(new ISearchEngineToModel() {
                @Override
                public Iterator<IndexEntry> getAllIndexEntries() {
                    return IndexDatabase.getAllIndexEntries();
                }

                @Override
                public int getNumberOfIndexEntries() {
                    try {
                        return IndexDatabase.getRowCount();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public int[] computeMinhashes(String query) {
                    try {
                        return hasher.computeHashes(query);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public Map<String, String> getData(Long id) {
                    try {
                        return IndexDatabase.getData(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);}

                }
            });
        }

        if (runServer) {
            // TODO: Implement server
        }

        if (numCrawlers > 0) {
            Spider spider = new Spider((url, metaData, text) -> {
                try {
                    int[] hashes = hasher.computeHashes(text);
                    IndexDatabase.write(url, metaData, hashes);
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
        } else if(!runServer) {
            for (int i = 3; i < args.length; i++) {
                String query = args[i];
                System.out.println("query: "+query);
                SearchResult result = searchEngine.search(query);
                System.out.println(result.toJSON());
            }
        }
    }



}
