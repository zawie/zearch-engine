package zearch.spider.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zearch.gram.Grammifier;
import zearch.spider.urlqueue.IQueue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Scraper {

    private IQueue<String> urlQueue;

    public Scraper(IQueue<String> urlQueue) {
        this.urlQueue = urlQueue;
    }

    public void scrapeUrl(String url) throws IOException {
        parse(url, Jsoup.connect(url).get());
    }

    public void scrapeFile(String filepath) throws IOException {
        parse(filepath, Jsoup.parse(new File(filepath)));
    }

    private void parse(String path, Document doc) {
        String title = doc.title();
        Elements metaTags = doc.getElementsByTag("meta");

        Map<String, String> metaData = new HashMap<>();
        metaData.put("title", title);
        StringBuilder metaText = new StringBuilder(title);
        for (Element metaTag : metaTags) {
            String name = metaTag.attr("name");
            String content = metaTag.attr("content");
            metaData.put(name,content);
            metaText.append(content).append(" ");
        }
        //TODO: Record meta data somewhere

        Elements linkTags = doc.getElementsByTag("a");
        for (Element linkTag : linkTags) {
            String href = linkTag.attr("href");
            if (href.matches("(\\.|/|.)(.*)")) {
                urlQueue.push(href.replaceFirst("(\\.|/|.)", path));
            } else {
                urlQueue.push(href);
            }
        }

        Map<String, Integer> textGrams = Grammifier.grammify(doc.text());
        Map<String, Integer> metaGrams = Grammifier.grammify(metaText.toString());
        //TODO: Record gram count somewhere
    }
}
