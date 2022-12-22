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
    public void searchTest() throws IOException, SQLException {
        Path path = Paths.get("tst/db/zearch-test");
        IndexDatabase.connect(path.toAbsolutePath().toString());

        Document doc1 = Scraper.getDocumentFromFilepath("tst/html/rome.html");
        Document doc2 = Scraper.getDocumentFromFilepath("tst/html/test.html");

        IndexDatabase.write("www.rome.com", Scraper.computeDocumentScore(doc1));
        IndexDatabase.write("www.movie.com", Scraper.computeDocumentScore(doc2));

        String query;
        SearchEngine engine = new SearchEngine();
        query = "roman empire information:";
        System.out.println(query + ": " + engine.search(query).toString());
        query = "watch video";
        System.out.println(query + ": " + engine.search(query).toString());

        IndexDatabase.close();
    }
}