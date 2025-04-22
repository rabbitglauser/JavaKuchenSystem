package com.tieinternational;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class HttpObjectClient {
    private final String baseUrl;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public class ErrorResponse {
        private String message;

        // Default constructor needed for Jackson
        public ErrorResponse() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public HttpObjectClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    public <T> T putObject(String path, Object requestObject, Class<T> responseType) throws IOException, InterruptedException {
        return sendObjectWithHttpClient("PUT", path, requestObject, responseType);
    }

    public <T> T deleteObject(String path, Class<T> responseType) throws IOException, InterruptedException {
        return sendObjectWithHttpClient("DELETE", path, null, responseType);
    }

    public <T> T getObject(String path, Class<T> responseType) throws IOException, InterruptedException {
        return sendObjectWithHttpClient("GET", path, null, responseType);
    }

    public <T> T postObject(String path, Object requestObject, Class<T> responseType) throws IOException, InterruptedException {
        return sendObjectWithHttpClient("POST", path, requestObject, responseType);
    }

    private <T> T sendObjectWithHttpClient(String method, String endpoint, Object requestBody, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json");

        HttpRequest request;
        if (requestBody != null) {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            request = requestBuilder
                    .method(method, HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();
        } else {
            request = requestBuilder
                    .method(method, HttpRequest.BodyPublishers.noBody())
                    .build();
        }

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Add logging to see the raw response
        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        // Check for error status codes
        if (response.statusCode() >= 400) {
            // Try to parse as ErrorResponse
            try {
                ErrorResponse errorResponse = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new IOException("Server error: " + errorResponse.getMessage());
            } catch (JsonProcessingException e) {
                // If can't parse as ErrorResponse, throw with raw body
                throw new IOException("Server error: " + response.body());
            }
        }

        // For success responses, parse as expected type
        return objectMapper.readValue(response.body(), responseType);
    }

    private <T> T sendObjectWithURLConnection(String method, String path, Object requestObject, Class<T> responseType) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(baseUrl + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            int connectTimeout = 5000;
            connection.setConnectTimeout(connectTimeout);
            int readTimeout = 8000;
            connection.setReadTimeout(readTimeout);

            if (requestObject != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] requestBytes = objectMapper.writeValueAsBytes(requestObject);
                    os.write(requestBytes, 0, requestBytes.length);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (InputStream is = connection.getInputStream()) {
                    return objectMapper.readValue(is, responseType);
                }
            } else {
                try (InputStream errorStream = connection.getErrorStream()) {
                    String errorResponse = errorStream != null ?
                            new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))
                                    .lines().collect(Collectors.joining("\n")) :
                            "No error details available";

                    throw new IOException("HTTP error code " + responseCode + ": " +
                            connection.getResponseMessage() + " - " + errorResponse);
                }
            }
        } catch (java.net.ConnectException e) {
            throw new IOException("Failed to connect to " + baseUrl + path + ": Connection refused. Is the server running?", e);
        } catch (java.net.SocketTimeoutException e) {
            throw new IOException("Connection to " + baseUrl + path + " timed out. Server may be overloaded or unreachable.", e);
        } catch (IOException e) {
            throw new IOException("Failed to connect to " + baseUrl + path + ": " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}