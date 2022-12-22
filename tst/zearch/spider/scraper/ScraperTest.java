package zearch.spider.scraper;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import zearch.spider.IPool;

import java.io.IOException;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class ScraperTest {

    @Test
    void parseLinksTest() throws IOException {
        Document doc = Scraper.getDocumentFromFilepath("tst/html/simple.html");
        Deque<URL> links = new LinkedList<>();
        Scraper.parseLinks(new URL("https://www.sample.com"), doc, new IPool<URL>() {
            @Override
            public void push(URL element) {
                links.push(element);
            }

            @Override
            public URL pull() {
                return links.pop();
            }
        });

        assertTrue(links.contains(new URL("https://www.sample.com/something1")));
        assertTrue(links.contains(new URL("https://www.sample.com/something2")));
        assertTrue(links.contains(new URL("https://www.sample.com/something3")));
        assertTrue(links.contains(new URL("https://www.zawie.io")));
    }
    @Test
    void parseMetaDataTest() throws IOException {
        Document doc = Scraper.getDocumentFromFilepath("tst/html/simple.html");
        Map<String, String> metaData = Scraper.parseMetaData(doc);
        assertEquals(metaData.get("title"), "Test Title");
        assertEquals(metaData.get("description"), "test description");
        assertEquals(metaData.get("keywords"), "test, sample, word");
    }

    @Test
    void computeDocumentScoreTest1() throws IOException {
        Document doc = Scraper.getDocumentFromFilepath("tst/html/simple.html");
        Map<String, Integer> gramScore = Scraper.computeDocumentScore("simple", doc);
        assertNull(gramScore.get("htm"));
        assertNotNull(gramScore.get("tes"));
    }

    @Test
    void computeDocumentScoreTest2() {
        try {
            Document doc = Scraper.getDocumentFromFilepath("tst/html/test.html");
            Map<String, Integer> gramScore = Scraper.computeDocumentScore("tst/html/test.html", doc);
            System.out.println(gramScore);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    @Test
    void computeDocumentScoreTest3() {
        try {
            Document doc = Scraper.getDocumentFromFilepath("tst/html/rome.html");
            Map<String, Integer> gramScore = Scraper.computeDocumentScore("www.fake-url.com", doc);
            System.out.println(gramScore);
        } catch (Exception e){
            System.out.println(e);
        }
    }

}