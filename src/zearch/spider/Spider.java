package zearch.spider;

import zearch.spider.robots.RobotsCache;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Spider {

    private Set<URL> visited;
    private Map<String, Queue<URL>> domainToURLs;
    private Queue<String> domainQueue;

    private Map<String, Long> ungroundTime;

    private ISpiderToModel model;

    private static final int MAX_DOMAIN_COUNT = 128;
    private static final int MAX_PATH_COUNT = 2048;


    private static RobotsCache robots = new RobotsCache(2048);

    public Spider(ISpiderToModel model) {
        this.model = model;
        this.visited = new HashSet<>();
        this.ungroundTime = new ConcurrentHashMap<>();
        this.domainToURLs = new ConcurrentHashMap<>();
        this.domainQueue = new ConcurrentLinkedQueue<>();
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
//                        System.out.println("Crawler " + crawlerId + " did not get a URL.");
                        sleep(1000);
                        continue;
                    }
                    try {
                        Scraper scraper = new Scraper(nextUrl);
                        model.index(nextUrl, scraper.parseMetaData(), scraper.getTextReader());
                        List<URL> links = scraper.parseLinks();
                        Collections.shuffle(links);
                        for (URL link : links)
                            offerURL(link);
                    } catch (org.jsoup.HttpStatusException e) {
                            int status = e.getStatusCode();
                            System.out.println(
                                    " ✕\t"+ status + "\t" + nextUrl
                            );
                            if (status != 403 && status != 404) {
                                ground(nextUrl);
                            }
                    } catch (java.net.UnknownHostException e) {
                        System.out.println(
                                " ✕\tUnknown host\t" + nextUrl
                        );
                        ground(nextUrl);
                    } catch (Exception e) {
                        System.out.println(
                                " ✕\tCrawler " + crawlerId + " encountered an exception on " + nextUrl
                        );
                        System.out.println("==================================");
                        e.printStackTrace();
                        System.out.println("==================================");
                        ground(nextUrl);
                    }
                }
            }).start();
        }
    }

    public void offerURL(URL url) {
        String domain = getDomain(url);
        if (visited.contains(url))
            return;
        if (!robots.isAllowed(url)) {
            return;
        }

       if (!domainToURLs.containsKey(domain)) {
           if (domainToURLs.keySet().size() < MAX_DOMAIN_COUNT) {
               domainQueue.add(domain);
               domainToURLs.put(domain, new ConcurrentLinkedQueue<>());
           } else {
               return;
           }
       }

       Queue<URL> urls = domainToURLs.get(domain);
       if (urls.size() < MAX_PATH_COUNT) {
           urls.add(url);
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
    private void ground(URL url) {
        long t = System.currentTimeMillis();
        String domain = getDomain(url);
        domainQueue.remove(domain);
        domainQueue.add(domain);
        ungroundTime.put(domain, Math.max(ungroundTime.getOrDefault(domain, (long) 0), t + 60000)); // 1 minute
    }

    private URL getNextUrl() throws NoSuchElementException {
        long t = System.currentTimeMillis();
        String domain = null;
        for (String d : domainQueue) {
            Long allowed = ungroundTime.getOrDefault(d, 0l);
            if (t > allowed) {
                domain = d;
                break;
            }
        }
        if (domain == null) {
           throw new NoSuchElementException();
        }

        Queue<URL> urls = domainToURLs.get(domain);
        if (urls == null) {
            throw new NoSuchElementException();
        }
        URL url = urls.poll();
        if (url == null) {
            throw new NoSuchElementException();
        }
        if (urls.isEmpty()) {
            domainToURLs.remove(domain);
        } else {
            domainQueue.add(domain);
        }
        visited.add(url);
        ungroundTime.put(domain, t + 1000);
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
