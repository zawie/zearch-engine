package zearch.spider.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zearch.gram.Grammifier;
import zearch.spider.IPool;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scraper {
    public static Document getDocumentFromURL(URL url) throws IOException {
        Connection conn = Jsoup.connect(url.toExternalForm());
        conn.timeout(15000); // 15 second timeout
        return conn.get();
    }

    public static Document getDocumentFromFilepath(String filepath) throws IOException {
        return Jsoup.parse(new File(filepath));
    }
    public static void parseLinks(URL path, Document doc, IPool<URL> urlDeque) throws MalformedURLException {
        String root = path.getProtocol()+"://"+path.getHost();
        Elements linkTags = doc.getElementsByTag("a");
        for (Element linkTag : linkTags) {
            String href = linkTag.attr("href");
            try {
                if (href.startsWith("#") || href.startsWith(".")) {
                    href = href.substring(1);
                    if (href.startsWith("/")) {
                        urlDeque.push(new URL(root+href));
                    } else {
                        urlDeque.push(new URL(root+"/"+href));
                    }
                } else if (href.startsWith("/"))
                    urlDeque.push(new URL(root+href));
                else if (href.matches("^([a-zA-Z0-9]+\\.)?[a-zA-Z0-9]+\\.[a-zA-Z0-9]+.*")) {
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

    public static Map<String,Integer> computeDocumentScore(String url, Document doc) {
        //Text Gram Score
        Map<String, Integer> textGramScore = Grammifier.computeGramScore(doc.text());

        //Title Gram Score
        String title = doc.title();
        if (title == null)
            title = "";
        Map<String, Integer> titleGramScore = Grammifier.computeGramScore(title);

        // Meta Gram Score
        StringBuilder metaText = new StringBuilder(url);
        Map<String, String> metaData = parseMetaData(doc);
        metaText.append(metaData.getOrDefault("title", "")).append(" ");
        metaText.append(metaData.getOrDefault("description", "")).append(" ");
        metaText.append(metaData.getOrDefault("keywords", "")).append(" ");

        Map<String, Integer> metaGramScore = Grammifier.computeGramScore(metaText.toString());

        //Compute weighted average of subscores
        Map<String, Integer> gramScore = new HashMap<>();
        Set<String> grams = new HashSet<>();
        grams.addAll(metaGramScore.keySet());
        grams.addAll(titleGramScore.keySet());
        grams.addAll(textGramScore.keySet());

        for (String gram : grams) {
            Integer score = (
                    200*titleGramScore.getOrDefault(gram, 0) +
                    500*metaGramScore.getOrDefault(gram, 0) +
                    300*textGramScore.getOrDefault(gram, 0)
                )/1000;
            score = Math.max(Math.min(score, 127), 0);
            gramScore.put(gram, score);
        }

        return gramScore;
    }
}
