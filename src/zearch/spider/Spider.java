package zearch.spider;

import zearch.spider.robots.RobotsCache;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Spider {

    private Set<URL> visited;
    private Queue<URL> urlQueue;

    private Map<String, Long> ungroundTime;

    private ISpiderToModel model;

    private static final int MAX_QUEUE_SIZE = 131072;


    private static RobotsCache robots = new RobotsCache(2048);

    public Spider(ISpiderToModel model) {
        this.model = model;
        this.visited = new HashSet<>();
        this.ungroundTime = new ConcurrentHashMap<>();
        this.urlQueue = new ConcurrentLinkedQueue<>();
    }

    public void startCrawling(int numThreads) {
        for (int id = 0; id < numThreads; id++) {
            final int crawlerId = id;
            new Thread(() -> {
                while (true) {
                    URL nextUrl = null;
                    try {
                        nextUrl = getNextUrl();
                    } catch (NoSuchElementException e) {
                        System.out.println("Crawler " + crawlerId + " did not get a URL.");
                        sleep(5000);
                        continue;
                    }
                    try {
                        Scraper scraper = new Scraper(nextUrl);
                        model.index(nextUrl, scraper.parseMetaData(), scraper.getText());
                        List<URL> links = scraper.parseLinks();
                        Collections.shuffle(links);
                        for (URL link : links) {
                            offerURL(new URL(link.getProtocol() +"://"+link.getHost()));
                            offerURL(new URL(link.getProtocol() +"://"+getDomain(link)));
                            offerURL(link);
                        }
                    } catch (org.jsoup.HttpStatusException e) {
                            int status = e.getStatusCode();
                            System.out.println(
                                    " ✕\t"+ status + "\t" + nextUrl
                            );
                            if (status != 403 && status != 404) {
                                ground(nextUrl, 60*1000);
                            }
                    } catch (java.net.UnknownHostException e) {
                        System.out.println(
                                " ✕\tUnknown host\t" + nextUrl
                        );
                        ground(nextUrl, 60*1000);
                    } catch (Exception e) {
                        System.out.println(
                                " ✕\tCrawler " + crawlerId + " encountered an exception on " + nextUrl
                        );
                        System.out.println("==================================");
                        e.printStackTrace();
                        System.out.println("==================================");
                        ground(nextUrl, 60*1000);
                    }
                }
            }).start();
        }
    }

    public void offerURL(URL url) {
        if (visited.contains(url))
            return;

       if (urlQueue.size() < MAX_QUEUE_SIZE) {
           urlQueue.add(url);
           visited.add(url);
       }
    }

    private String getDomain(URL url) {
        String host = url.getHost();
        String[] parts = host.split("\\.");
        if (parts.length < 2) {
            return host;
        }
        return parts[parts.length - 2] + "." + parts[parts.length - 1];
    }

    private void ground(URL url, long ms) {
        long t = System.currentTimeMillis();
        String domain = getDomain(url);
        ungroundTime.put(domain, Math.max(ungroundTime.getOrDefault(domain, (long) 0), t + ms));
    }

    private URL getNextUrl() throws NoSuchElementException {
        System.out.println("(get) Queue size: "+urlQueue.size());
        long t = System.currentTimeMillis();
        URL url;
        while((url = urlQueue.remove()) != null) {
            String domain = getDomain(url);
            Long allowed = ungroundTime.getOrDefault(domain, 0l);
            if (t > allowed) {
                break;
            } else {
                urlQueue.add(url);
            }
        }

        if (url == null) {
            throw new NoSuchElementException();
        }

        if (!robots.isAllowed(url))
            return getNextUrl();
        ground(url, 100);
        return url;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
