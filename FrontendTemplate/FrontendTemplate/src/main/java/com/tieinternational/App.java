package com.tieinternational;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task; // Add this import
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private final HttpObjectClient client = new HttpObjectClient("http://localhost:8080");
    private String currentUserRole; // To store the current user's role ("admin" or "employee")

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cake Production Management");

        // Show the login screen first
        showLoginScene(primaryStage);

        primaryStage.show();
    }

    public static class ErrorResponse {
        private String message;

        // Default constructor for Jackson
        public ErrorResponse() {}

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    private void showLoginScene(Stage stage) {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        Label statusLabel = new Label();

        // Add a "Continue without server" option for development
        Button continueOfflineButton = new Button("Continue in Demo Mode");
        continueOfflineButton.setOnAction(event -> {
            // Set a default role for testing UI
            currentUserRole = "admin"; // or "employee" to test different views

            // Show the dashboard
            showDashboard(stage);

            // Inform the user they're in demo mode
            Alert demoAlert = new Alert(Alert.AlertType.INFORMATION);
            demoAlert.setTitle("Demo Mode");
            demoAlert.setHeaderText(null);
            demoAlert.setContentText("You are now using the application in demo mode. Backend functionality is limited.");
            demoAlert.showAndWait();
        });

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Show loading indicator
            statusLabel.setText("Connecting to server...");
            statusLabel.setStyle("-fx-text-fill: blue;");
            loginButton.setDisable(true);

            // Use a background thread for the network request
            Task<LoginResponse> task = new Task<>() {
                @Override
                protected LoginResponse call() throws Exception {
                    LoginRequest loginRequest = new LoginRequest(username, password);
                    return client.postObject("/login", loginRequest, LoginResponse.class);
                }
            };

            task.setOnSucceeded(e -> {
                LoginResponse response = task.getValue();

                if ("success".equals(response.getStatus())) {
                    currentUserRole = response.getRole();
                    statusLabel.setText("");

                    // Show success popup
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Login Successful");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Welcome, " + username + "! Role: " + currentUserRole);
                    successAlert.showAndWait();

                    // Show the dashboard
                    showDashboard(stage);
                } else {
                    statusLabel.setText("Invalid username or password");
                    statusLabel.setStyle("-fx-text-fill: red;");

                    // Show failure popup
                    Alert failureAlert = new Alert(Alert.AlertType.ERROR);
                    failureAlert.setTitle("Login Failed");
                    failureAlert.setHeaderText(null);
                    failureAlert.setContentText("Invalid username or password. Please try again.");
                    failureAlert.showAndWait();
                }

                loginButton.setDisable(false);
            });

            task.setOnFailed(e -> {
                Throwable exc = task.getException();
                exc.printStackTrace();

                statusLabel.setText("Server connection error");
                statusLabel.setStyle("-fx-text-fill: red;");
                loginButton.setDisable(false);

                // Show a more informative error message
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Server Connection Error");
                errorAlert.setHeaderText("Cannot connect to the backend server");
                errorAlert.setContentText(
                        "The application cannot connect to the server at http://localhost:8080.\n\n" +
                                "Please check that:\n" +
                                "1. The backend server is running\n" +
                                "2. The server is accessible on port 8080\n" +
                                "3. No firewall is blocking the connection\n\n" +
                                "Error details: " + exc.getMessage()
                );
                errorAlert.showAndWait();
            });

            // Start the background task
            new Thread(task).start();
        });

        loginLayout.getChildren().addAll(
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                loginButton,
                new Separator(), // Add a separator
                continueOfflineButton,
                statusLabel
        );

        Scene loginScene = new Scene(loginLayout, 300, 250);
        stage.setScene(loginScene);
    }

    private void showDashboard(Stage stage) {
        BorderPane dashboardLayout = new BorderPane();
        dashboardLayout.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome to the Cake Management System!");

        // Add logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> showLoginScene(stage));

        HBox headerBox = new HBox(10);
        headerBox.getChildren().addAll(welcomeLabel, logoutButton);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        dashboardLayout.setTop(headerBox);

        VBox menuLayout = new VBox(10);
        menuLayout.setPadding(new Insets(10));

        if ("admin".equals(currentUserRole)) {
            Button manageCakesButton = new Button("Manage Cakes");
            manageCakesButton.setOnAction(event -> showManageCakesScene(stage));

            Button manageUsersButton = new Button("Manage Users");
            manageUsersButton.setOnAction(event -> showManageUsersScene(stage));

            menuLayout.getChildren().addAll(manageCakesButton, manageUsersButton);

        } else if ("employee".equals(currentUserRole)) {
            Button viewCakesButton = new Button("View Cakes");
            viewCakesButton.setOnAction(event -> showViewCakesScene(stage));

            menuLayout.getChildren().add(viewCakesButton);
        }

        dashboardLayout.setCenter(menuLayout);

        Scene dashboardScene = new Scene(dashboardLayout, 400, 300);
        stage.setScene(dashboardScene);
    }

    private void showManageCakesScene(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Manage Cakes");
        layout.getChildren().add(titleLabel);

        TextField cakeNameField = new TextField();
        cakeNameField.setPromptText("Cake Name");

        TextField cakeDescriptionField = new TextField();
        cakeDescriptionField.setPromptText("Cake Description");

        TextField cakeDurationField = new TextField();
        cakeDurationField.setPromptText("Duration (in minutes)");

        // Table for listing cakes
        TableView<Cake> cakeTable = new TableView<>();
        cakeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Cake, String> nameColumn = new TableColumn<>("Cake Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Cake, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Cake, String> durationColumn = new TableColumn<>("Duration (mins)");
        durationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getDurationInMinutes())));

        cakeTable.getColumns().addAll(nameColumn, descriptionColumn, durationColumn);

        // Load cakes into the table
        try {
            loadCakesIntoTable(cakeTable);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load existing cakes.");
            alert.show();
        }

        // Add the table to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(cakeTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(200); // Set preferred height for scroll pane

        Button createCakeButton = new Button("Create Cake");
        createCakeButton.setOnAction(event -> {
            String name = cakeNameField.getText();
            String description = cakeDescriptionField.getText();

            // Validate input
            if (name.isEmpty() || description.isEmpty() || cakeDurationField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled out.");
                alert.show();
                return;
            }

            // Validate that duration is a number
            int duration;
            try {
                duration = Integer.parseInt(cakeDurationField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Duration must be a number.");
                alert.show();
                return;
            }

            try {
                Cake newCake = new Cake(name, description, duration);

                // Create a Task to handle the network operation in a background thread
                Task<Cake> createCakeTask = new Task<>() {
                    @Override
                    protected Cake call() throws Exception {
                        return client.postObject("/cakes", newCake, Cake.class);
                    }
                };

                createCakeTask.setOnSucceeded(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cake created successfully!");
                    alert.show();

                    // Clear fields after successful creation
                    cakeNameField.clear();
                    cakeDescriptionField.clear();
                    cakeDurationField.clear();

                    // Refresh the cake table
                    try {
                        loadCakesIntoTable(cakeTable);
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to refresh cake list.");
                        errorAlert.show();
                    }
                });

                createCakeTask.setOnFailed(e -> {
                    Throwable exception = createCakeTask.getException();
                    exception.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create cake: " + exception.getMessage());
                    alert.show();
                });

                new Thread(createCakeTask).start();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create cake: " + e.getMessage());
                alert.show();
            }
        });

        Button updateCakeButton = new Button("Update Selected Cake");
        updateCakeButton.setOnAction(event -> {
            Cake selectedCake = cakeTable.getSelectionModel().getSelectedItem();
            if (selectedCake == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No cake selected to update.");
                alert.show();
                return;
            }

            String updatedName = cakeNameField.getText().isEmpty() ? selectedCake.getName() : cakeNameField.getText();
            String updatedDescription = cakeDescriptionField.getText().isEmpty() ? selectedCake.getDescription() : cakeDescriptionField.getText();
            int updatedDuration;

            try {
                updatedDuration = cakeDurationField.getText().isEmpty()
                        ? selectedCake.getDurationInMinutes()
                        : Integer.parseInt(cakeDurationField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Duration must be a number.");
                alert.show();
                return;
            }

            Cake updatedCake = new Cake(updatedName, updatedDescription, updatedDuration);

            Task<Void> updateTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    client.putObject("/cakes/" + selectedCake.getId(), updatedCake, Void.class);
                    return null;
                }
            };

            updateTask.setOnSucceeded(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cake updated successfully!");
                alert.show();

                // Refresh the cake table
                try {
                    loadCakesIntoTable(cakeTable);
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to refresh cake list.");
                    errorAlert.show();
                }

                // Clear input fields
                cakeNameField.clear();
                cakeDescriptionField.clear();
                cakeDurationField.clear();
            });

            updateTask.setOnFailed(e -> {
                Throwable exception = updateTask.getException();
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update cake: " + exception.getMessage());
                alert.show();
            });

            new Thread(updateTask).start();
        });

        Button deleteCakeButton = new Button("Delete Selected Cake");
        deleteCakeButton.setOnAction(event -> {
            Cake selectedCake = cakeTable.getSelectionModel().getSelectedItem();
            if (selectedCake == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No cake selected to delete.");
                alert.show();
                return;
            }

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this cake?");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Task<Void> deleteTask = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            client.deleteObject("/cakes/" + selectedCake.getId(), Void.class);
                            return null;
                        }
                    };

                    deleteTask.setOnSucceeded(e -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cake deleted successfully!");
                        alert.show();

                        // Refresh the cake table
                        try {
                            loadCakesIntoTable(cakeTable);
                        } catch (IOException | InterruptedException ex) {
                            ex.printStackTrace();
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to refresh cake list.");
                            errorAlert.show();
                        }
                    });

                    deleteTask.setOnFailed(e -> {
                        Throwable exception = deleteTask.getException();
                        exception.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete cake: " + exception.getMessage());
                        alert.show();
                    });

                    new Thread(deleteTask).start();
                }
            });
        });

        layout.getChildren().addAll(
                cakeNameField,
                cakeDescriptionField,
                cakeDurationField,
                createCakeButton,
                updateCakeButton,
                deleteCakeButton,
                new Label("Existing Cakes:"),
                scrollPane
        );

        // Add a back button
        addBackButton(layout, stage);

        Scene manageCakesScene = new Scene(layout, 600, 400);
        stage.setScene(manageCakesScene);
    }

    // Add this new method to update the table
    private void loadCakesIntoTable(TableView<Cake> cakeTable) throws IOException, InterruptedException {
        // Clear existing items
        cakeTable.getItems().clear();

        // Load cakes into the table
        Cake[] cakes = client.getObject("/cakes", Cake[].class);
        cakeTable.getItems().addAll(cakes);
    }

    private void refreshCakeList(VBox layout) throws IOException {
        // Remove existing cake labels (skip the title, form fields, buttons)
        int startIndex = layout.getChildren().size();
        // Find where the cake list starts - it's after the "Existing Cakes:" label
        for (int i = 0; i < layout.getChildren().size(); i++) {
            if (layout.getChildren().get(i) instanceof Label) {
                Label label = (Label) layout.getChildren().get(i);
                if ("Existing Cakes:".equals(label.getText())) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        // Remove all children from the startIndex up to the back button
        while (startIndex < layout.getChildren().size() - 1) {
            layout.getChildren().remove(startIndex);
        }

        // Reload cakes
        try {
            Cake[] cakes = client.getObject("/cakes", Cake[].class);

            if (cakes.length == 0) {
                layout.getChildren().add(startIndex, new Label("No cakes available."));
            } else {
                for (Cake cake : cakes) {
                    Label cakeLabel = new Label("- " + cake.getName() + ": " +
                            cake.getDescription() + " (" +
                            cake.getDurationInMinutes() + " mins)");
                    layout.getChildren().add(startIndex++, cakeLabel);
                }
            }
        } catch (IOException | InterruptedException e) { // Add InterruptedException here
            e.printStackTrace();
            layout.getChildren().add(startIndex, new Label("Could not load existing cakes."));
        }
    }


    private void showManageUsersScene(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Manage Users");
        layout.getChildren().add(titleLabel);

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

            // Validate input
            if (username.isEmpty() || password.isEmpty() || role == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled out.");
                alert.show();
                return;
            }

            try {
                User newUser = new User(username, password, role);
                client.postObject("/users", newUser, User.class);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User created successfully!");
                alert.show();

                // Clear fields after successful creation
                usernameField.clear();
                passwordField.clear();
                roleComboBox.setValue(null);
            } catch (IOException | InterruptedException e) { // Add InterruptedException here
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create user: " + e.getMessage());
                alert.show();
            }
        });

        layout.getChildren().addAll(usernameField, passwordField, roleComboBox, createUserButton);

        // Add a back button
        addBackButton(layout, stage);

        Scene manageUsersScene = new Scene(layout, 400, 300);
        stage.setScene(manageUsersScene);
    }

    private void showViewCakesScene(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Available Cakes");
        layout.getChildren().add(titleLabel);  // Add title first

        try {
            Cake[] cakes = client.getObject("/cakes", Cake[].class);
            if (cakes.length == 0) {
                layout.getChildren().add(new Label("No cakes available."));
            } else {
                for (Cake cake : cakes) {
                    Label cakeLabel = new Label("- " + cake.getName() + ": " + cake.getDescription() + " (" + cake.getDurationInMinutes() + " mins)");
                    layout.getChildren().add(cakeLabel);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to fetch cakes.");
            alert.show();
        }


        // Add a back button
        addBackButton(layout, stage);

        Scene viewCakesScene = new Scene(layout, 400, 300);
        stage.setScene(viewCakesScene);
    }

    private void addBackButton(VBox layout, Stage stage) {
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(event -> showDashboard(stage));
        layout.getChildren().add(backButton);
    }

    public static void main(String[] args) {
        launch(args);
    }
}