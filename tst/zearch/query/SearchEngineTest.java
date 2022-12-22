package zearch.query;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import zearch.index.IndexDatabase;
import zearch.spider.scraper.Scraper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SearchEngineTest {

    @Test
    public void searchTestLocal() throws IOException, SQLException {
        Path path = Paths.get("tst/db/zearch-test");
        IndexDatabase.connect(path.toAbsolutePath().toString());

        Document doc1 = Scraper.getDocumentFromFilepath("tst/html/rome.html");
        Document doc2 = Scraper.getDocumentFromFilepath("tst/html/test.html");

        IndexDatabase.write("www.rome.com", Scraper.computeDocumentScore(doc1));
        IndexDatabase.write("www.movie.com", Scraper.computeDocumentScore(doc2));

        String query;
        query = "roman empire information:";
        System.out.println(query + ": " + SearchEngine.search(query));
        query = "watch video";
        System.out.println(query + ": " + SearchEngine.search(query));

        IndexDatabase.close();
    }

    @Test
    public void searchTestWeb() throws IOException, SQLException {
        Path path = Paths.get("tst/db/zearch-test");
        IndexDatabase.connect(path.toAbsolutePath().toString());

        Document doc1 = Scraper.getDocumentFromURL("https://en.wikipedia.org/wiki/Rome");
        Document doc2 = Scraper.getDocumentFromURL("https://www.youtube.com");

        IndexDatabase.write("https://en.wikipedia.org/wiki/Rome", Scraper.computeDocumentScore(doc1));
        IndexDatabase.write("https://www.youtube.com", Scraper.computeDocumentScore(doc2));

        String query;
        query = "roman empire information:";
        System.out.println(query + ": " + SearchEngine.search(query));
        query = "watch video";
        System.out.println(query + ": " + SearchEngine.search(query));

        IndexDatabase.close();
    }
}