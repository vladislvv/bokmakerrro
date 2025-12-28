package com.betapp.controller;

import com.betapp.model.User;
import com.betapp.util.DBConnection;
import com.betapp.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Введите логин и пароль");
            return;
        }

        System.out.println("Попытка входа: " + username);

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?"
            );
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getDouble("balance")
                );

                System.out.println("Успешный вход! Пользователь: " + user.getUsername() +
                        ", ID: " + user.getId() + ", Баланс: " + user.getBalance());

                // Используем SceneManager для перехода
                SceneManager.showMainScene(user);

            } else {
                System.out.println("Неверные учетные данные для пользователя: " + username);
                showAlert("Ошибка", "Неверный логин или пароль");
            }

        } catch (Exception e) {
            System.err.println("Ошибка подключения к базе:");
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка подключения к базе данных");
        }
    }

    // ===== ПЕРЕХОД К РЕГИСТРАЦИИ =====
    @FXML
    public void goToRegister() {
        SceneManager.showRegisterScene();
    }

    // ===== ALERT =====
    private void showAlert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.show();
    }
}