package zearch.spider.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zearch.gram.Grammifier;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;

public class Scraper {
    public static Document getDocumentFromURL(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static Document getDocumentFromFilepath(String filepath) throws IOException {
        return Jsoup.parse(new File(filepath));
    }
    public static void parseLinks(String path, Document doc, Queue<String> urlQueue) {
        String root = path;
        if (root.endsWith("/"))
            root = path.substring(0, path.length() - 1);
        Elements linkTags = doc.getElementsByTag("a");
        for (Element linkTag : linkTags) {
            String href = linkTag.attr("href");
            if (href.startsWith("#") || href.startsWith("."))
                urlQueue.add(root + href.substring(1));
            else if (href.startsWith("/"))
                urlQueue.add(root+href);
            else
                urlQueue.add(href);
        }
    }

    public static Map<String, String> parseMetaData(Document doc) {
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

        return metaData;
    }

    public static Map<String,Short> parseMetaGrams(Document doc) {
        String title = doc.title();
        Elements metaTags = doc.getElementsByTag("meta");

        StringBuilder metaText = new StringBuilder(title);

        for (Element metaTag : metaTags) {
            String content = metaTag.attr("content");
            metaText.append(content).append(" ");
        }

        return Grammifier.grammify(metaText.toString());
    }

    public static Map<String,Short> parseTextGrams(Document doc) {
        return Grammifier.grammify(doc.text());
    }
}
