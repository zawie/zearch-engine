package zearch;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import zearch.index.IndexDatabase;
import zearch.index.URLScorePair;
import zearch.query.SearchEngine;
import zearch.server.Server;
import zearch.spider.Spider;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        String dbFilepath = args[0];
        Integer numCrawlers = Integer.parseInt(args[1]);

        IndexDatabase.connect(dbFilepath);

        List<String> urls = new LinkedList<>();
        for (int i = 2; i < args.length; i++) {
            urls.add(args[i]);
        }

        Server.start();
        Spider.run(numCrawlers, urls);
    }
}
