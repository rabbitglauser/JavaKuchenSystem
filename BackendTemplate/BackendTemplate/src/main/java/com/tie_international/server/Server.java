package com.tie_international.server;

import com.tie_international.DAOs.CakeDAO;
import com.tie_international.DAOs.UserDAO;
import com.tie_international.model.Cake;
import com.tie_international.model.User;
import com.tie_international.RequestUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress; // Add this import
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080, 0, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("Server is running on port 8080...");
            CakeDAO cakeDAO = new CakeDAO();
            UserDAO userDAO = new UserDAO();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        handleClient(clientSocket, cakeDAO, userDAO);
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException | SQLException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, CakeDAO cakeDAO, UserDAO userDAO) throws IOException, SQLException {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            System.out.println("Received request: " + requestLine);
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            routeRequest(method, path, in, out, cakeDAO, userDAO);
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
            if (out != null) {
                sendErrorResponse(out, 500, "Internal server error: " + e.getMessage());
            }
        } finally {
            // Don't close the streams here, they'll be closed in the routeRequest methods
            clientSocket.close();
        }
    }

    private static void handleCreateUser(PrintWriter out, UserDAO userDAO, String requestBody) throws SQLException {
        try {
            User newUser = deserializeUserFromJson(requestBody);
            User createdUser = userDAO.createUser(newUser);

            if (createdUser != null) {
                // Convert user to JSON and send back
                JSONObject userJson = new JSONObject();
                userJson.put("id", createdUser.getId());
                userJson.put("username", createdUser.getUsername());
                userJson.put("password", ""); // Don't send back the password
                userJson.put("role", createdUser.getRole());

                sendJsonResponse(out, 201, userJson.toString());
            } else {
                sendErrorResponse(out, 500, "Failed to create user");
            }
        } catch (IllegalArgumentException e) {
            sendBadRequestResponse(out, e.getMessage());
        } catch (SQLException e) {
            sendErrorResponse(out, 500, "Failed to create user: " + e.getMessage());
        }
    }

    private static User deserializeUserFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            String role = jsonObject.getString("role");
            return new User(0, username, password, role); // ID will be assigned by the database
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON format for User", e);
        }
    }

    private static void routeRequest(String method, String path, BufferedReader in, PrintWriter out, CakeDAO cakeDAO, UserDAO userDAO) throws IOException, SQLException {
        // Don't add CORS headers here - they're added in the response methods

        if ("GET".equalsIgnoreCase(method) && "/cakes".equals(path)) {
            handleGetCakes(out, cakeDAO);
        } else if ("POST".equalsIgnoreCase(method) && "/cakes".equals(path)) {
            String requestBody = processRequestBody(in, out);
            if (requestBody != null) {
                handlePostCake(out, cakeDAO, requestBody);
            }
        } else if ("DELETE".equalsIgnoreCase(method) && path.startsWith("/cakes/")) {
            handleDeleteCake(out, cakeDAO, path);
        } else if ("POST".equalsIgnoreCase(method) && "/login".equals(path)) {
            String requestBody = processRequestBody(in, out);
            if (requestBody != null) {
                handleLogin(out, userDAO, requestBody);
            }
        } else if ("POST".equalsIgnoreCase(method) && "/users".equals(path)) {
            String requestBody = processRequestBody(in, out);
            if (requestBody != null) {
                handleCreateUser(out, userDAO, requestBody);
            }
        } else if ("OPTIONS".equalsIgnoreCase(method)) {
            sendOptionsResponse(out);
        } else {
            sendNotFoundResponse(out);
        }
    }

    private static String processRequestBody(BufferedReader in, PrintWriter out) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;

        // Process HTTP headers
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println("Header: " + line);
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
            }
        }

        // If Content-Length header was provided, read exactly that many characters
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int bytesRead = in.read(buffer, 0, contentLength);
            requestBody.append(buffer, 0, bytesRead);
        } else {
            // Fallback to reading what's available
            while (in.ready() && (line = in.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String body = requestBody.toString().trim();
        if (body.isEmpty()) {
            System.out.println("Error: Request body is empty.");
            sendBadRequestResponse(out, "Request body is empty");
            return null;
        }

        System.out.println("Raw Request Body: " + body);
        return body;
    }

    private static void handleGetCakes(PrintWriter out, CakeDAO cakeDAO) throws SQLException {
        try {
            List<Cake> cakes = cakeDAO.getAll();
            String jsonResponse = serializeCakesToJson(cakes);
            sendJsonResponse(out, 200, jsonResponse);
        } catch (SQLException e) {
            sendErrorResponse(out, 500, "Failed to fetch cakes: " + e.getMessage());
        }
    }

    private static void handlePostCake(PrintWriter out, CakeDAO cakeDAO, String requestBody) throws SQLException {
        try {
            Cake newCake = deserializeCakeFromJson(requestBody);
            cakeDAO.save(newCake);
            sendJsonResponse(out, 201, "{\"message\":\"Cake created successfully.\"}");
        } catch (IllegalArgumentException e) {
            sendBadRequestResponse(out, e.getMessage());
        } catch (SQLException e) {
            sendErrorResponse(out, 500, "Failed to create cake: " + e.getMessage());
        }
    }

    private static void handleDeleteCake(PrintWriter out, CakeDAO cakeDAO, String path) throws SQLException {
        try {
            String[] pathParts = path.split("/");
            int cakeId = Integer.parseInt(pathParts[2]);
            cakeDAO.delete(cakeId);
            sendJsonResponse(out, 200, "{\"message\":\"Cake deleted successfully.\"}");
        } catch (NumberFormatException e) {
            sendBadRequestResponse(out, "Invalid cake ID format");
        } catch (SQLException e) {
            sendErrorResponse(out, 500, "Failed to delete cake: " + e.getMessage());
        }
    }

    private static void handleLogin(PrintWriter out, UserDAO userDAO, String requestBody) throws SQLException {
        try {
            String username = RequestUtils.extractUsername(requestBody);
            String password = RequestUtils.extractPassword(requestBody);

            AuthController authController = new AuthController();
            String role = authController.login(username, password);

            // Send success response
            String response = "{\"status\":\"success\",\"role\":\"" + role + "\"}";
            sendJsonResponse(out, 200, response);
        } catch (IllegalArgumentException e) {
            sendBadRequestResponse(out, e.getMessage());
        } catch (SQLException e) {
            sendErrorResponse(out, 401, "Invalid username or password");
        }
    }

    private static String serializeCakesToJson(List<Cake> cakes) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < cakes.size(); i++) {
            Cake cake = cakes.get(i);
            json.append("{")
                    .append("\"id\":").append(cake.getId()).append(",")
                    .append("\"name\":\"").append(escapeJsonString(cake.getName())).append("\",")
                    .append("\"description\":\"").append(escapeJsonString(cake.getDescription())).append("\",")
                    .append("\"durationInMinutes\":").append(cake.getDurationInMinutes())
                    .append("}");
            if (i < cakes.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    // Helper method to escape JSON strings
    private static String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '/':
                    escaped.append("\\/");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }


    private static Cake deserializeCakeFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");
            int durationInMinutes = jsonObject.getInt("durationInMinutes");
            return new Cake(0, name, description, durationInMinutes);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON format for Cake", e);
        }
    }

    private static void sendJsonResponse(PrintWriter out, int statusCode, String responseBody) {
        String statusMessage = getStatusMessage(statusCode);

        // Start Response
        out.println("HTTP/1.1 " + statusCode + " " + statusMessage);
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length);
        // Add CORS headers
        out.println("Access-Control-Allow-Origin: *");
        out.println("Access-Control-Allow-Methods: GET, POST, DELETE, PUT, OPTIONS");
        out.println("Access-Control-Allow-Headers: Content-Type");
        // End of Headers
        out.println();
        // Add Body
        out.print(responseBody); // IMPORTANT: Changed from println to print to avoid extra newline
        out.flush();
        // Don't close the writer here as it will close the underlying socket
    }

    private static void sendBadRequestResponse(PrintWriter out, String message) {
        String responseBody = "{\"status\":\"error\",\"message\":\"" + message + "\"}";
        sendJsonResponse(out, 400, responseBody);
    }

    private static void sendErrorResponse(PrintWriter out, int statusCode, String message) {
        String responseBody = "{\"status\":\"error\",\"message\":\"" + message + "\"}";
        sendJsonResponse(out, statusCode, responseBody);
    }

    private static void sendNotFoundResponse(PrintWriter out) {
        sendJsonResponse(out, 404, "{\"status\":\"error\",\"message\":\"Endpoint not found.\"}");
    }

    private static void sendOptionsResponse(PrintWriter out) {
        out.println("HTTP/1.1 204 No Content");
        out.println("Access-Control-Allow-Origin: *");
        out.println("Access-Control-Allow-Methods: GET, POST, DELETE, PUT, OPTIONS");
        out.println("Access-Control-Allow-Headers: Content-Type");
        out.println("Content-Length: 0");
        out.println();
        out.flush();
        // Don't close the writer here
    }

    private static String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200: return "OK";
            case 201: return "Created";
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            case 204: return "No Content";
            default: return "Unknown";
        }
    }
}