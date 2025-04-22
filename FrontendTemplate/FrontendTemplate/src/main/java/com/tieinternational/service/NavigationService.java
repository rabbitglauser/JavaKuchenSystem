package com.tieinternational.service;

import com.tieinternational.controller.LoginController;
import com.tieinternational.controller.DashboardController;
import com.tieinternational.controller.ManageCakesController;
import com.tieinternational.controller.ManageUsersController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class NavigationService {
    private final Stage stage;
    private final Map<String, SceneFactory> sceneFactories = new HashMap<>();
    private String currentUserRole;

    public NavigationService(Stage stage) {
        this.stage = stage;
        initializeScenes();
    }

    private void initializeScenes() {
        // Register scenes
        sceneFactories.put("login", () -> new LoginController(this).getScene());
        sceneFactories.put("dashboard", () -> new DashboardController(this, currentUserRole).getScene());
        sceneFactories.put("manageCakes", () -> new ManageCakesController(this).getScene());
        sceneFactories.put("manageUsers", () -> new ManageUsersController(this).getScene());
    }

    public void navigateTo(String sceneName) {
        if (!sceneFactories.containsKey(sceneName)) {
            throw new IllegalArgumentException("Scene not found: " + sceneName);
        }

        Scene scene = sceneFactories.get(sceneName).createScene();
        stage.setScene(scene);
    }

    public void setCurrentUserRole(String role) {
        this.currentUserRole = role;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    @FunctionalInterface
    private interface SceneFactory {
        Scene createScene();
    }
}