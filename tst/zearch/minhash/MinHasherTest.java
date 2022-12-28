package zearch.minhash;

import org.junit.jupiter.api.Test;
import zearch.spider.Scraper;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MinHasherTest {
    MinHasher minHasher = new MinHasher();

    @Test
    void testSmallText() throws IOException {
        System.out.println(Arrays.toString(minHasher.computeHashes("this is a sample string")));
    }

    @Test
    void testLargeText() throws IOException {
        Scraper scraper = new Scraper(new URL("https://en.wikipedia.org/wiki/Philosophy"));
        String text = scraper.getText();
        System.out.println(Arrays.toString(minHasher.computeHashes(text)));
    }

}