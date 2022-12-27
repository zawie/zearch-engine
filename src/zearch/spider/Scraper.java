package zearch.spider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Scraper {

    private URL url;
    private Document document;
    public Scraper(URL url) throws IOException {
        this.url = url;
        Connection conn = Jsoup.connect(url.toExternalForm());
        conn.timeout(15000); // 15 second
        this.document = conn.get();
    }
    public List<URL> parseLinks() {
        String root = url.getProtocol()+"://"+url.getHost();
        Elements linkTags = document.getElementsByTag("a");
        List<URL> urls = new LinkedList<>();
        for (Element linkTag : linkTags) {
            String href = linkTag.attr("href");
            try {
                if (href.startsWith("#") || href.startsWith(".")) {
                    href = href.substring(1);
                    if (href.startsWith("/")) {
                        urls.add(new URL(root+href));
                    } else {
                        urls.add(new URL(root+"/"+href));
                    }
                } else if (href.startsWith("/"))
                    urls.add(new URL(root+href));
                else if (href.matches("^([a-zA-Z0-9]+\\.)?[a-zA-Z0-9]+\\.[a-zA-Z0-9]+.*")) {
                    urls.add(new URL("https://"+href));
                    urls.add(new URL("http://"+href));
                } else {
                    urls.add(new URL(href));
                }
            } catch (MalformedURLException e){
                // System.out.println("\t" + href + " is not a valid link.");
            }
        }
        return urls;
    }

    public Map<String, String> parseMetaData() {
        String title = document.title();
        Map<String, String> metaData = new HashMap<>();
        if (title != null)
            metaData.put("title", title);

        Elements metaTags = document.getElementsByTag("meta");
        for (Element metaTag : metaTags) {
            String name = metaTag.attr("name");
            String content = metaTag.attr("content");
            if (name.toLowerCase().equals("title"))
                continue;
            metaData.put(name.toLowerCase(),content);
        }

        return metaData;
    }

    public Reader getTextReader() {
        return new StringReader(document.text());
    }
}