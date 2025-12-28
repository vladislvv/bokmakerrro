package com.betapp.util;

import com.betapp.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;
    private static Object currentUser;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        System.out.println("SceneManager: PrimaryStage установлен");
    }

    public static void setCurrentUser(Object user) {
        currentUser = user;
    }

    public static Object getCurrentUser() {
        return currentUser;
    }

    public static void showLoginScene() {
        System.out.println("SceneManager: Показываем окно логина");
        loadScene("/fxml/Login.fxml", "Вход в систему", null);
    }

    public static void showRegisterScene() {
        System.out.println("SceneManager: Показываем окно регистрации");
        loadScene("/fxml/Register.fxml", "Регистрация", null);
    }

    public static void showMainScene(Object userData) {
        setCurrentUser(userData);
        System.out.println("SceneManager: Показываем главное окно");
        loadScene("/fxml/Main.fxml", "Главная страница", userData);
    }

    public static void showBetHistoryScene() {
        System.out.println("SceneManager: Показываем историю ставок");
        loadScene("/fxml/BetHistory.fxml", "История ставок", currentUser);
    }

    public static void showMakeBetScene() {
        System.out.println("SceneManager: Показываем окно ставок");
        loadScene("/fxml/MakeBet.fxml", "Сделать ставку", currentUser);
    }

    public static void showDepositScene() {
        System.out.println("SceneManager: Показываем окно пополнения");
        loadScene("/fxml/Deposit.fxml", "Пополнить баланс", currentUser);
    }

    public static void showSimulateResultsScene() {
        System.out.println("SceneManager: Показываем окно симуляции");
        loadScene("/fxml/SimulateResults.fxml", "Симуляция результатов", currentUser);
    }

    // Универсальный метод для загрузки любой сцены
    public static void loadScene(String fxmlPath, String title, Object controllerData) {
        System.out.println("Загрузка сцены: " + fxmlPath);

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));

            if (loader.getLocation() == null) {
                System.err.println("Файл FXML не найден: " + fxmlPath);
                return;
            }

            System.out.println("FXML найден: " + loader.getLocation());

            Scene scene = new Scene(loader.load());
            System.out.println("Сцена создана успешно");

            // Применяем CSS
            try {
                String css = SceneManager.class.getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(css);
                System.out.println("CSS применен: " + css);
            } catch (NullPointerException e) {
                System.err.println("CSS файл не найден!");
            }

            // Передаем данные контроллеру
            Object controller = loader.getController();
            if (controllerData != null) {
                if (controller instanceof MainController) {
                    ((MainController) controller).setUser(controllerData);
                } else if (controller instanceof BetHistoryController) {
                    ((BetHistoryController) controller).setUser(controllerData);
                } else if (controller instanceof MakeBetController) {
                    ((MakeBetController) controller).setUser(controllerData);
                } else if (controller instanceof DepositController) {
                    ((DepositController) controller).setUser(controllerData);
                } else if (controller instanceof SimulateResultsController) {
                    ((SimulateResultsController) controller).setUser(controllerData);
                }
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("Окно показано: " + title);

        } catch (IOException e) {
            System.err.println("Ошибка загрузки FXML: " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("FXML файл не найден: " + fxmlPath);
            e.printStackTrace();
        }
    }
}