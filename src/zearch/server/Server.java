package zearch.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import zearch.engine.SearchResult;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private IServerToModel model;
    private HttpServer server;

    private static final int PORT = 80;
    public Server(IServerToModel model) throws IOException {
        this.model = model;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        String path = "/search/";
        server.createContext(path, (exchange -> {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin" , "*");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Access-Control-Allow-Headers");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "X-Requested-With");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Access-Control-Request-Method");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Access-Control-Request-Headers");
                exchange.sendResponseHeaders(204, -1);
                return;
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI()
                        .getQuery()
                        .substring("query=".length())
                        .replaceAll("\\+", " ");

                String responseText = null;
                try {
                    responseText = model.search(query).toJSON();
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);// 500 Server Error
                    return;
                }
                headers.set("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));

                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));
    }
    public void start() throws IOException {
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}