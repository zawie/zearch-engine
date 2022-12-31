package zearch.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

public class Server {

    private IServerToModel model;
    private HttpsServer server;
    private SSLContext sslContext;
    private static final int PORT = 443;
    public Server(IServerToModel model) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        this.model = model;
        this.server = HttpsServer.create(new InetSocketAddress(PORT), 0);
        this.sslContext = SSLContext.getInstance("SSL");

        // Initialise the keystore
        char[] password = "simulator".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream("lig.keystore");
        ks.load(fis, password);

        // Set up the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // Set up the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // Initialise the SSL context
                    SSLContext c = SSLContext.getDefault();
                    SSLEngine engine = c.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Get the default parameters
                    SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParameters);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

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
                    responseText = "{\"error\": \""+e.toString()+"\"}";
                    exchange.sendResponseHeaders(500, responseText.length()); // 500 Server Error
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseText.getBytes());
                    output.flush();
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