package com.tie_international;

import com.sun.net.httpserver.HttpServer;
import com.tie_international.Handlers.RootHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        //Routes werden hier definiert
        server.createContext("/", new RootHandler());
        //server.createContext("/kuchen",new KuchenHandler()); als Beispiel
        server.setExecutor(null); // Use default executor
        server.start();
        System.out.println("Server started on port " + port);
    }


}

