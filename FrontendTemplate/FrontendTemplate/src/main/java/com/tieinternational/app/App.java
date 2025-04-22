package com.tieinternational.app;

import com.tieinternational.service.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cake Production Management");

        // Initialize navigation service with the primary stage
        NavigationService navigationService = new NavigationService(primaryStage);

        // Navigate to the login screen
        navigationService.navigateTo("login");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}