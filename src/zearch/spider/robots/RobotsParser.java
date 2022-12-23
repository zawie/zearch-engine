package zearch.spider.robots;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import zearch.spider.util.Cache;

public class RobotsParser {
    public static Set<String> getDisallowedPaths(URL url) {
        URL robotsUrl;
        try {
            robotsUrl = new URL(url.getProtocol() + "://" + url.getHost() + "/robots.txt");
        } catch (MalformedURLException e){
            return new HashSet<>();
        }

        String contents = getContents(robotsUrl);
        if (contents == null)
            return new HashSet<>();

        Scanner scanner = new Scanner(contents.toLowerCase());
        boolean read = false;

        Set<String> disallowed = new HashSet<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("user-agent:"))
                read = false;
            if (line.startsWith("user-agent: *"))
                read = true;
            if (!read)
                continue;
            if (line.startsWith("disallow:")) {
                String path = line.substring("disallow:".length()).trim();
                if (!path.endsWith("/"))
                    path += "/";
                if (path.startsWith("*"))
                    path = "."+path;
                disallowed.add(path);
            }
        }
        scanner.close();

        return disallowed;
    }

    private static String getContents(URL url) {
        String content = null;
        URLConnection connection = null;
        try {
            connection = url.openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        } catch ( Exception ex ) {
            return null;
        }
        return content;
    }

}
