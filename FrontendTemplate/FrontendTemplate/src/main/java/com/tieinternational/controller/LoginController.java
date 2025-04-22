package com.tieinternational.controller;

import com.tieinternational.client.HttpObjectClient;
import com.tieinternational.request.LoginRequest;
import com.tieinternational.response.LoginResponse;
import com.tieinternational.service.NavigationService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    private final NavigationService navigationService;
    private final HttpObjectClient clientService = new HttpObjectClient("http://localhost:8080");

    public LoginController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    public Scene getScene() {
        VBox loginLayout = new VBox(10);
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label statusLabel = new Label();

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            statusLabel.setText("Connecting to server...");
            loginButton.setDisable(true);

            Task<LoginResponse> task = new Task<>() {
                @Override
                protected LoginResponse call() throws Exception {
                    return clientService.postObject("/login", new LoginRequest(username, password), LoginResponse.class);
                }
            };

            task.setOnSucceeded(e -> {
                LoginResponse response = task.getValue();
                if ("success".equals(response.getStatus())) {
                    navigationService.setCurrentUserRole(response.getRole());
                    navigationService.navigateTo("dashboard");
                } else {
                    statusLabel.setText("Invalid username or password");
                }
                loginButton.setDisable(false);
            });

            task.setOnFailed(e -> {
                statusLabel.setText("Server connection error");
                loginButton.setDisable(false);
            });

            new Thread(task).start();
        });

        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, statusLabel);
        return new Scene(loginLayout, 300, 250);
    }
}