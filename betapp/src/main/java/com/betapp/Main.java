package com.betapp;

import com.betapp.util.DatabaseInitializer;
import com.betapp.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=== Запуск приложения BetApp ===");
        System.out.println("JavaFX версия: " + System.getProperty("javafx.version"));
        System.out.println("Java версия: " + System.getProperty("java.version"));

        try {
            System.out.println("Инициализация базы данных...");
            DatabaseInitializer.initialize();

            System.out.println("Настройка основного окна...");
            SceneManager.setPrimaryStage(primaryStage);

            System.out.println("Показ окна логина...");
            SceneManager.showLoginScene();

            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);
            primaryStage.setTitle("BetApp - Букмекерская контора");

            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Приложение закрывается...");
                Platform.exit();
                System.exit(0);
            });

            System.out.println("=== Приложение успешно запущено ===");
            System.out.println("Ожидание действий пользователя...");

        } catch (Exception e) {
            System.err.println("Критическая ошибка при запуске:");
            e.printStackTrace();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка запуска");
                alert.setHeaderText("Не удалось запустить приложение");
                alert.setContentText("Ошибка: " + e.getMessage());
                alert.showAndWait();
                Platform.exit();
            });
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Запуск JavaFX приложения ===");
        System.out.println("Параметры запуска: " + String.join(" ", args));

        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Фатальная ошибка:");
            e.printStackTrace();
        }
    }
}