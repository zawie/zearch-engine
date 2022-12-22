package zearch.spider.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zearch.gram.Grammifier;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Deque;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;

public class Scraper {
    public static Document getDocumentFromURL(URL url) throws IOException {
        return Jsoup.connect(url.toExternalForm()).get();
    }

    public static Document getDocumentFromFilepath(String filepath) throws IOException {
        return Jsoup.parse(new File(filepath));
    }
    public static void parseLinks(URL path, Document doc, Deque<URL> urlDeque) throws MalformedURLException {
        String root = path.getProtocol()+"://"+path.getHost();
        Elements linkTags = doc.getElementsByTag("a");
        for (Element linkTag : linkTags) {
            String href = linkTag.attr("href");
            try {
                if (href.startsWith("#") || href.startsWith("."))
                    urlDeque.push(new URL(root+"/"+href.substring(1)));
                else if (href.startsWith("/"))
                    urlDeque.push(new URL(root+href));
                else if (href.startsWith("www.")) {
                    urlDeque.push(new URL("https://"+href));
                    urlDeque.push(new URL("http://"+href));
                } else {
                    urlDeque.push(new URL(href));
                }
            } catch (MalformedURLException e){
//                System.out.println("\t" + href + " is not a valid link.");
            } catch (IllegalStateException e) {
                // nothing
            }
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

    public static Map<String,Integer> computeDocumentScore(Document doc) {
        //Text Gram Score
        Map<String, Integer> textGramScore = Grammifier.computeGramScore(doc.text());

        // Meta Gram Score
        StringBuilder metaText = new StringBuilder();
        Map<String, String> metaData = parseMetaData(doc);
        metaText.append(metaData.getOrDefault("title", "")).append(" ");
        metaText.append(metaData.getOrDefault("description", "")).append(" ");
        metaText.append(metaData.getOrDefault("keywords", "")).append(" ");

        Map<String, Integer> metaGramScore = Grammifier.computeGramScore(metaText.toString());

        //Average meta and text gram score.
        Map<String, Integer> gramScore = new HashMap<>();
        for (String gram : metaGramScore.keySet()) {
            gramScore.put(gram, metaGramScore.get(gram));
        }
        for (String gram : textGramScore.keySet()) {
            Integer score = gramScore.getOrDefault(gram, 0) + (textGramScore.get(gram));
            gramScore.put(gram, score/2);
        }

        return gramScore;
    }
}
