package com.tieinternational;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ManageUsersController {
    private final NavigationService navigationService;
    private final HttpObjectClient clientService = new HttpObjectClient("http://localhost:8080");

    public ManageUsersController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    public Scene getScene() {
        VBox layout = new VBox(10);

        Label titleLabel = new Label("Manage Users");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("admin", "employee");
        roleComboBox.setPromptText("Select Role");

        Button createUserButton = new Button("Create User");
        createUserButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled out.", null);
                return;
            }

            User newUser = new User(username, password, role);

            try {
                // Check if username already exists
                User[] existingUsers = clientService.getObject("/users", User[].class);
                boolean usernameExists = false;
                for (User existingUser : existingUsers) {
                    if (existingUser.getUsername().equals(username)) {
                        usernameExists = true;
                        break;
                    }
                }

                if (usernameExists) {
                    showAlert(Alert.AlertType.ERROR, "User Creation Error", "Username already exists. Please choose a different username.", null);
                    return;
                }

                // Proceed with user creation
                clientService.postObject("/users", newUser, User.class);
                showAlert(Alert.AlertType.INFORMATION, "Success", "User created successfully!", null);
                usernameField.clear();
                passwordField.clear();
                roleComboBox.setValue(null);
            } catch (IOException | InterruptedException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create user.", e.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> navigationService.navigateTo("dashboard"));

        layout.getChildren().addAll(titleLabel, usernameField, passwordField, roleComboBox, createUserButton, backButton);
        return new Scene(layout, 400, 300);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}