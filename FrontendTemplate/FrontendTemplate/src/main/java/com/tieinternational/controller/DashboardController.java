package com.tieinternational.controller;

import com.tieinternational.service.NavigationService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardController {

    private final NavigationService navigationService;
    private final String currentUserRole;

    public DashboardController(NavigationService navigationService, String currentUserRole) {
        this.navigationService = navigationService;
        this.currentUserRole = currentUserRole;
    }

    public Scene getScene() {
        BorderPane layout = new BorderPane();
        Label welcomeLabel = new Label("Welcome to the Cake Management System!");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> navigationService.navigateTo("login"));

        HBox header = new HBox(10, welcomeLabel, logoutButton);
        layout.setTop(header);

        VBox menu = new VBox(10);
        if ("admin".equals(currentUserRole)) {
            Button manageCakesButton = new Button("Manage Cakes");
            manageCakesButton.setOnAction(event -> navigationService.navigateTo("manageCakes"));

            Button manageUsersButton = new Button("Manage Users");
            manageUsersButton.setOnAction(event -> navigationService.navigateTo("manageUsers"));

            menu.getChildren().addAll(manageCakesButton, manageUsersButton);
        } else if ("employee".equals(currentUserRole)) {
            Button viewCakesButton = new Button("View Cakes");
            viewCakesButton.setOnAction(event -> navigationService.navigateTo("manageCakes"));

            menu.getChildren().add(viewCakesButton);
        }

        layout.setCenter(menu);
        return new Scene(layout, 400, 300);
    }
}