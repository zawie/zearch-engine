package zearch.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import zearch.index.IndexDatabase;
import zearch.index.URLScorePair;
import zearch.query.SearchEngine;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException, SQLException {

        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        String path = "/api/search/";

        IndexDatabase.connect(args[0]);

        server.createContext(path, (exchange -> {
            Headers headers = exchange.getResponseHeaders();
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI()
                        .getQuery()
                        .substring("query=".length())
                        .replaceAll("\\+", " ");
                System.out.println("query: "+query);
                List<URLScorePair> results = null;
                try {
                    results = SearchEngine.search(query);
                } catch (SQLException e) {
                    exchange.sendResponseHeaders(500, -1);// 500 Server Error
                }
                headers.set("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));

                List<String> urls = new ArrayList<>(results.size());
                List<String> scores = new ArrayList<>(results.size());

                for (URLScorePair result : results) {
                    urls.add("\""+result.getURL()+"\"");
                    scores.add(result.getScore().toString());
                }

                String responseText = String.format("{\"query\": \"%s\", results:[\n%s\n], scores:[%s]}",
                        query,
                        String.join(", ", urls),
                        String.join(", ", scores));

                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));


        server.setExecutor(null); // creates a default executor
        server.start();

    }
}
