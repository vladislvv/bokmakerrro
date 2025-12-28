package com.betapp.controller;

import com.betapp.util.DBConnection;
import com.betapp.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()){
            showAlert("Ошибка","Введите логин и пароль");
            return;
        }

        try(Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(username,password,balance) VALUES(?,?,?)"
            );
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setDouble(3,1000); // стартовый баланс
            ps.executeUpdate();

            showAlert("Успех","Регистрация прошла успешно!");
            goToLogin();
        } catch(Exception e) {
            e.printStackTrace();
            showAlert("Ошибка","Пользователь уже существует или ошибка базы");
        }
    }

    @FXML
    public void goToLogin() {
        SceneManager.showLoginScene();
    }

    private void showAlert(String title,String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}