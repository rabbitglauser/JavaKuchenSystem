package com.tieinternational;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;



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

    // Add this method to create a search panel
    private HBox createSearchPanel(TableView<Cake> tableView) {
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(5));
        searchBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label searchLabel = new Label("Search:");
        TextField searchField = new TextField();
        searchField.setPromptText("Enter name, description or duration");
        searchField.setPrefWidth(250);

        ComboBox<String> searchTypeCombo = new ComboBox<>();
        searchTypeCombo.getItems().addAll("All", "Name", "Description", "Duration");
        searchTypeCombo.setValue("All");

        Button searchButton = new Button("Search");
        Button clearButton = new Button("Clear");

        searchBox.getChildren().addAll(searchLabel, searchField, searchTypeCombo, searchButton, clearButton);

        // Add search functionality
        searchButton.setOnAction(event -> {
            String searchTerm = searchField.getText().toLowerCase().trim();
            String searchType = searchTypeCombo.getValue();

            if (searchTerm.isEmpty()) {
                refreshTableView(tableView);
                return;
            }

            // Get all cakes and filter them
            try {
                Cake[] allCakes = client.getObject("/cakes", Cake[].class);
                tableView.getItems().clear();

                for (Cake cake : allCakes) {
                    boolean matches = false;

                    if (searchType.equals("All") || searchType.equals("Name")) {
                        if (cake.getName().toLowerCase().contains(searchTerm)) {
                            matches = true;
                        }
                    }

                    if ((searchType.equals("All") || searchType.equals("Description")) && !matches) {
                        if (cake.getDescription().toLowerCase().contains(searchTerm)) {
                            matches = true;
                        }
                    }

                    if ((searchType.equals("All") || searchType.equals("Duration")) && !matches) {
                        String durationStr = String.valueOf(cake.getDurationInMinutes());
                        if (durationStr.contains(searchTerm)) {
                            matches = true;
                        }
                    }

                    if (matches) {
                        tableView.getItems().add(cake);
                    }
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to search cakes: " + e.getMessage());
                alert.show();
            }
        });

        // Clear search and show all cakes
        clearButton.setOnAction(event -> {
            searchField.clear();
            refreshTableView(tableView);
        });

        return searchBox;
    }

    private void showManageCakesScene(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Manage Cakes");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        layout.getChildren().add(titleLabel);

        // Create form for adding new cakes
        VBox formBox = new VBox(5);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label formLabel = new Label("Add New Cake:");
        formLabel.setStyle("-fx-font-weight: bold;");

        TextField cakeNameField = new TextField();
        cakeNameField.setPromptText("Cake Name");

        TextField cakeDescriptionField = new TextField();
        cakeDescriptionField.setPromptText("Cake Description");

        TextField cakeDurationField = new TextField();
        cakeDurationField.setPromptText("Duration (in minutes)");

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
                        try {
                            // Use your ErrorResponse class to parse the response
                            ErrorResponse response = client.postObject("/cakes", newCake, ErrorResponse.class);

                            // If we get here without an exception, it was successful
                            // The response contains the success message
                            if (response != null && response.getMessage() != null &&
                                    response.getMessage().contains("Cake created successfully")) {
                                // Success case - return the cake we sent
                                return newCake;
                            } else {
                                // Got a response but it's not the expected success message
                                throw new Exception("Unexpected response from server: " +
                                        (response != null ? response.getMessage() : "null"));
                            }
                        } catch (IOException e) {
                            // If it's a standard IOException, wrap it with a more descriptive message
                            throw new Exception("Error communicating with the server: " + e.getMessage(), e);
                        }
                    }
                };

                createCakeTask.setOnSucceeded(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cake created successfully!");
                    alert.show();

                    // Clear fields after successful creation
                    cakeNameField.clear();
                    cakeDescriptionField.clear();
                    cakeDurationField.clear();

                    // Refresh the cake list
                    try {
                        refreshCakeTable(layout);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                createCakeTask.setOnFailed(e -> {
                    Throwable exception = createCakeTask.getException();
                    exception.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Failed to create cake: " + exception.getMessage());
                    alert.show();
                });

                // Start the background task
                new Thread(createCakeTask).start();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create cake: " + e.getMessage());
                alert.show();
            }
        });

        formBox.getChildren().addAll(formLabel, cakeNameField, cakeDescriptionField, cakeDurationField, createCakeButton);
        layout.getChildren().add(formBox);

        // Separator between form and table
        layout.getChildren().add(new Separator());

        // Create TableView for existing cakes
        Label existingCakesLabel = new Label("Existing Cakes:");
        existingCakesLabel.setStyle("-fx-font-weight: bold;");
        layout.getChildren().add(existingCakesLabel);

        // Create table with appropriate columns
        TableView<Cake> cakeTable = new TableView<>();

        // Define columns
        TableColumn<Cake, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setPrefWidth(100);

        TableColumn<Cake, String> descColumn = new TableColumn<>("Description");
        descColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        descColumn.setPrefWidth(200);

        TableColumn<Cake, String> durationColumn = new TableColumn<>("Duration (mins)");
        durationColumn.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().getDurationInMinutes())));
        durationColumn.setPrefWidth(100);

        // Add action column for edit/delete buttons
        TableColumn<Cake, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(120);
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                // Edit button action
                editButton.setOnAction(event -> {
                    Cake cake = getTableView().getItems().get(getIndex());
                    showEditCakeDialog(cake, getTableView());
                });

                // Delete button action
                deleteButton.setOnAction(event -> {
                    Cake cake = getTableView().getItems().get(getIndex());
                    deleteCake(cake, getTableView());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });

        cakeTable.getColumns().addAll(nameColumn, descColumn, durationColumn, actionColumn);

        // After creating the cakeTable and before adding it to the layout:

        // Add the search panel
        HBox searchPanel = createSearchPanel(cakeTable);
        layout.getChildren().add(searchPanel);

        // Load data into the table
        try {
            Cake[] cakes = client.getObject("/cakes", Cake[].class);
            cakeTable.getItems().addAll(cakes);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            layout.getChildren().add(new Label("Could not load existing cakes."));
        }

        // Add the table to the layout
        layout.getChildren().add(cakeTable);

        // Add a back button
        addBackButton(layout, stage);

        // Create a ScrollPane to make the entire layout scrollable if needed
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        Scene manageCakesScene = new Scene(scrollPane, 600, 600);
        stage.setScene(manageCakesScene);
    }

    // Method to show edit cake dialog
    private void showEditCakeDialog(Cake cake, TableView<Cake> tableView) {
        // Create a dialog
        Dialog<Cake> dialog = new Dialog<>();
        dialog.setTitle("Edit Cake");
        dialog.setHeaderText("Edit Cake Details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(cake.getName());
        TextField descriptionField = new TextField(cake.getDescription());
        TextField durationField = new TextField(String.valueOf(cake.getDurationInMinutes()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Duration (mins):"), 0, 2);
        grid.add(durationField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field
        nameField.requestFocus();

        // Convert the result to a cake when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int duration = Integer.parseInt(durationField.getText());
                    cake.setName(nameField.getText());
                    cake.setDescription(descriptionField.getText());
                    cake.setDurationInMinutes(duration);
                    return cake;
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Duration must be a number.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(updatedCake -> {
            // Send update to server
            Task<Void> updateTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        client.putObject("/cakes/" + updatedCake.getId(), updatedCake, ErrorResponse.class);
                        return null;
                    } catch (Exception e) {
                        throw new Exception("Failed to update cake: " + e.getMessage());
                    }
                }
            };

            updateTask.setOnSucceeded(e -> {
                refreshTableView(tableView);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cake updated successfully!");
                alert.showAndWait();
            });

            updateTask.setOnFailed(e -> {
                Throwable exception = updateTask.getException();
                Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
                alert.showAndWait();
            });

            new Thread(updateTask).start();
        });
    }

    // Method to delete a cake
    private void deleteCake(Cake cake, TableView<Cake> tableView) {
        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Cake");
        confirmDialog.setHeaderText("Confirm Deletion");
        confirmDialog.setContentText("Are you sure you want to delete the cake: " + cake.getName() + "?");

        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Create task for deleting the cake
                Task<Void> deleteTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            client.deleteObject("/cakes/" + cake.getId(), ErrorResponse.class);

                            // Refresh the table view after deletion
                            tableView.getItems().remove(cake);
                            return null;
                        } catch (Exception e) {
                            throw new Exception("Failed to delete cake: " + e.getMessage());
                        }
                    }
                };

                deleteTask.setOnSucceeded(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cake deleted successfully!");
                    alert.showAndWait();
                });

                deleteTask.setOnFailed(e -> {
                    Throwable exception = deleteTask.getException();
                    Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
                    alert.showAndWait();
                });

                new Thread(deleteTask).start();
            }
        });
    }

    // Modified refresh method to update the TableView
    private void refreshCakeTable(VBox layout) throws IOException {
        // Find the TableView in the layout
        TableView<Cake> cakeTable = null;
        for (int i = 0; i < layout.getChildren().size(); i++) {
            if (layout.getChildren().get(i) instanceof TableView) {
                cakeTable = (TableView<Cake>) layout.getChildren().get(i);
                break;
            }
        }

        if (cakeTable != null) {
            refreshTableView(cakeTable);
        }
    }

    // Method to refresh any TableView with cakes
    private void refreshTableView(TableView<Cake> cakeTable) {
        // Clear existing items
        cakeTable.getItems().clear();

        // Load fresh data
        try {
            Cake[] cakes = client.getObject("/cakes", Cake[].class);
            cakeTable.getItems().addAll(cakes);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // We could show an error message here if needed
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not refresh cake list.");
            alert.show();
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
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        layout.getChildren().add(titleLabel);

        // Create TableView for existing cakes
        TableView<Cake> cakeTable = new TableView<>();

        // Define columns
        TableColumn<Cake, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setPrefWidth(100);

        TableColumn<Cake, String> descColumn = new TableColumn<>("Description");
        descColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        descColumn.setPrefWidth(200);

        TableColumn<Cake, String> durationColumn = new TableColumn<>("Duration (mins)");
        durationColumn.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().getDurationInMinutes())));
        durationColumn.setPrefWidth(100);

        cakeTable.getColumns().addAll(nameColumn, descColumn, durationColumn);

        // Set table properties
        cakeTable.setPrefHeight(200);  // Set a height that allows scrolling
        cakeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add the search panel
        HBox searchPanel = createSearchPanel(cakeTable);
        layout.getChildren().add(searchPanel);

        // Load data into the table
        try {
            Cake[] cakes = client.getObject("/cakes", Cake[].class);
            cakeTable.getItems().addAll(cakes);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            layout.getChildren().add(new Label("Could not load existing cakes."));
        }

        // Add the table to the layout
        layout.getChildren().add(cakeTable);

        // Add a back button
        addBackButton(layout, stage);

        // Create a ScrollPane to make the entire layout scrollable if needed
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        Scene viewCakesScene = new Scene(scrollPane, 600, 600);
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