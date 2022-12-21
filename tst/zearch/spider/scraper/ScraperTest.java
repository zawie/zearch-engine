package zearch.spider.scraper;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class ScraperTest {

    @Test
    void parseLinksTest() throws IOException {
        Document doc = Scraper.getDocumentFromFilepath("tst/html/simple.html");
        Queue<String> links = new LinkedList<>();
        Scraper.parseLinks("https://www.sample.com", doc, links);
        System.out.println(links);
        assertTrue(links.contains("https://www.sample.com/something1"));
        assertTrue(links.contains("https://www.sample.com/something2"));
        assertTrue(links.contains("https://www.sample.com/something3"));
        assertTrue(links.contains("https://www.zawie.io"));
    }
}