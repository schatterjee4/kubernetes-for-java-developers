package com.example.kubia;

import com.example.blinktclient.Blinkt;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class App {

    private static final byte[] LARGE_BLOB = new byte[1024*1024];

    private static Map<HttpExchange, byte[]> activeContexts = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Kubia server starting...");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            activeContexts.put(t, LARGE_BLOB.clone());
            System.out.println("Received request from " + t.getRemoteAddress().getAddress().getHostAddress());
            Blinkt.flashLED();
            String response = "You've hit " + InetAddress.getLocalHost().getHostName() + "\n";
            sendResponse(t, response);
        }
    }

    private static void sendResponse(HttpExchange t, String response) throws IOException {
        byte[] responseBytes = response.getBytes();
        t.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(responseBytes);
        }
    }

}