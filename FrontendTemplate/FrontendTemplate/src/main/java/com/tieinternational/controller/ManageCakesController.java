package com.tieinternational.controller;

import com.tieinternational.model.Cake;
import com.tieinternational.client.HttpObjectClient;
import com.tieinternational.service.NavigationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ManageCakesController {
    private final NavigationService navigationService;
    private final HttpObjectClient clientService = new HttpObjectClient("http://localhost:8080");

    public ManageCakesController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    public Scene getScene() {
        VBox layout = new VBox(10);

        Label titleLabel = new Label("Manage Cakes");

        TextField cakeNameField = new TextField();
        cakeNameField.setPromptText("Cake Name");

        TextField cakeDescriptionField = new TextField();
        cakeDescriptionField.setPromptText("Cake Description");

        TextField cakeDurationField = new TextField();
        cakeDurationField.setPromptText("Duration (in minutes)");

        TableView<Cake> cakeTable = new TableView<>();
        cakeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Cake, String> nameColumn = new TableColumn<>("Cake Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<Cake, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        TableColumn<Cake, String> durationColumn = new TableColumn<>("Duration (mins)");
        durationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getDurationInMinutes())));
        cakeTable.getColumns().addAll(nameColumn, descriptionColumn, durationColumn);

        try {
            loadCakesIntoTable(cakeTable);
        } catch (IOException | InterruptedException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load cakes", e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane(cakeTable);

        Button createCakeButton = new Button("Create Cake");
        createCakeButton.setOnAction(event -> {
            String name = cakeNameField.getText().trim();
            String description = cakeDescriptionField.getText().trim();
            String durationText = cakeDurationField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || durationText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled out.", null);
                return;
            }

            try {
                int duration = Integer.parseInt(durationText);
                if (duration <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Duration must be a positive number.", null);
                    return;
                }

                // Check for duplicate names
                Cake[] existingCakes = clientService.getObject("/cakes", Cake[].class);
                for (Cake existingCake : existingCakes) {
                    if (existingCake.getName().equalsIgnoreCase(name)) {
                        showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Cake with this name already exists.", null);
                        return;
                    }
                }

                // Proceed with creation
                Cake newCake = new Cake(name, description, duration);
                String response = clientService.postObject("/cakes", newCake, String.class);
                if (!response.isEmpty()) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        HttpObjectClient.ErrorResponse errorResponse = objectMapper.readValue(response, HttpObjectClient.ErrorResponse.class);
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to create cake.", errorResponse.getMessage());
                    } catch (Exception e) {
                        // Fallback to generic error message
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to create cake.", "Unexpected error occurred.");
                    }
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cake created successfully!", null);
                    cakeNameField.clear();
                    cakeDescriptionField.clear();
                    cakeDurationField.clear();
                    loadCakesIntoTable(cakeTable);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Duration must be a valid number.", null);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create cake.", e.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> navigationService.navigateTo("dashboard"));

        layout.getChildren().addAll(titleLabel, cakeNameField, cakeDescriptionField, cakeDurationField, createCakeButton, scrollPane, backButton);
        return new Scene(layout, 600, 400);
    }

    private void loadCakesIntoTable(TableView<Cake> cakeTable) throws IOException, InterruptedException {
        cakeTable.getItems().clear();
        Cake[] cakes = clientService.getObject("/cakes", Cake[].class);
        cakeTable.getItems().addAll(cakes);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}