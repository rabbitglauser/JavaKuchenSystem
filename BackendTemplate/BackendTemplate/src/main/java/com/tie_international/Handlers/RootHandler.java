package com.tie_international.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tie_international.server.DBConnection;

import java.io.IOException;
import java.io.OutputStream;

public class RootHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        boolean connectionstatus = DBConnection.getInstance().testConnection();
        String response = "";
        if (connectionstatus) {

            response = "Backend Server Running, DB Connection Good";
        } else {
            response = "Backend Server Running, DB Connection Bad";
        }
        System.out.println(response);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}