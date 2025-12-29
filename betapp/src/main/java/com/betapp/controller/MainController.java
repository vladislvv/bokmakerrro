package com.betapp.controller;

import com.betapp.model.User;
import com.betapp.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private Label balanceLabel;

    private User currentUser;

    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            updateUI();
        }
    }

    private void updateUI() {
        if (currentUser != null) {
            welcomeLabel.setText("Добро пожаловать, " + currentUser.getUsername() + "!");
            balanceLabel.setText(String.format("Баланс: $%.2f", currentUser.getBalance()));
        }
    }

    @FXML
    public void logout() {
        SceneManager.showLoginScene();
    }

    @FXML
    public void showBetHistory() {
        SceneManager.showBetHistoryScene();
    }

    @FXML
    public void showMakeBet() {
        SceneManager.showMakeBetScene();
    }

    @FXML
    public void showDeposit() {
        SceneManager.showDepositScene();
    }

    @FXML
    public void showSimulateResults() {
        // Простая симуляция без отдельного окна
        simulateAllMatches();
    }

    public void updateBalance() {
        if (currentUser != null) {
            balanceLabel.setText(String.format("Баланс: $%.2f", currentUser.getBalance()));
        }
    }

    private void simulateAllMatches() {
        com.betapp.dao.BetDAO betDAO = new com.betapp.dao.BetDAO();
        com.betapp.dao.MatchDAO matchDAO = new com.betapp.dao.MatchDAO();

        java.util.List<com.betapp.model.Match> matches = matchDAO.getSimulatableMatches();

        if (matches.isEmpty()) {
            showAlert("Информация", "Нет матчей для симуляции", javafx.scene.control.Alert.AlertType.INFORMATION);
            return;
        }

        // Подтверждение
        javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Симуляция результатов");
        confirmAlert.setHeaderText("Подтвердите симуляцию");
        confirmAlert.setContentText(String.format(
                "Вы уверены, что хотите симулировать результаты %d матчей?\n" +
                        "Это определит все ставки и выплатит выигрыши.",
                matches.size()
        ));

        if (confirmAlert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK) {
            int count = 0;
            for (com.betapp.model.Match match : matches) {
                if (betDAO.processMatchComplete(match.getId())) {
                    count++;
                }
            }

            showAlert("Успех", String.format(
                    "Симуляция завершена!\n\n" +
                            "Обработано матчей: %d\n" +
                            "Все ставки определены, выигрыши выплачены.",
                    count
            ), javafx.scene.control.Alert.AlertType.INFORMATION);

            if (currentUser != null) {
                com.betapp.dao.UserDAO userDAO = new com.betapp.dao.UserDAO();
                User updatedUser = userDAO.getUserById(currentUser.getId());
                if (updatedUser != null) {
                    currentUser.setBalance(updatedUser.getBalance());
                    updateUI();
                }
            }
        }
    }

    private void showAlert(String title, String message, javafx.scene.control.Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}