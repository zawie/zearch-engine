package zearch.spider.robots;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class RobotsParserTest {

    @Test
    void wikipediaTest() throws MalformedURLException {
        assertFalse(RobotsParser.SINGLETON.isAllowed(new URL("https://www.wikipedia.org/api/")));
        assertFalse(RobotsParser.SINGLETON.isAllowed(new URL("https://www.wikipedia.org/api")));
        assertTrue(RobotsParser.SINGLETON.isAllowed(new URL("https://www.wikipedia.org/wiki/")));
        assertTrue(RobotsParser.SINGLETON.isAllowed(new URL("https://www.wikipedia.org/wiki")));
    }
}