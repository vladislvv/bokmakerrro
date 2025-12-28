package com.betapp.controller;

import com.betapp.dao.UserDAO;
import com.betapp.model.User;
import com.betapp.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class DepositController {

    @FXML private Label balanceLabel;
    @FXML private TextField amountField;
    @FXML private Button depositButton;
    @FXML private GridPane amountsGrid;

    private User currentUser;
    private UserDAO userDAO = new UserDAO();

    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            updateUI();
            setupQuickAmountButtons();
        }
    }

    private void updateUI() {
        if (currentUser != null) {
            balanceLabel.setText(String.format("Текущий баланс: $%.2f", currentUser.getBalance()));
        }
    }

    private void setupQuickAmountButtons() {
        double[] quickAmounts = {10, 25, 50, 100, 250, 500};

        amountsGrid.getChildren().clear();

        for (int i = 0; i < quickAmounts.length; i++) {
            Button quickButton = new Button("$" + (int)quickAmounts[i]);
            quickButton.getStyleClass().add("quick-amount-button");
            quickButton.setPrefWidth(80);
            quickButton.setPrefHeight(40);

            final double amount = quickAmounts[i];
            quickButton.setOnAction(e -> {
                amountField.setText(String.valueOf(amount));
            });

            amountsGrid.add(quickButton, i % 3, i / 3);
        }
    }

    @FXML
    public void deposit() {
        if (amountField.getText().isEmpty()) {
            showAlert("Ошибка", "Введите сумму для пополнения");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());

            if (amount <= 0) {
                showAlert("Ошибка", "Сумма должна быть больше 0");
                return;
            }

            if (amount > 10000) {
                showAlert("Ошибка", "Максимальная сумма пополнения: $10,000");
                return;
            }

            // Пополняем баланс
            currentUser.deposit(amount);

            // Обновляем баланс в базе данных
            boolean success = userDAO.updateBalance(currentUser.getId(), currentUser.getBalance());

            if (success) {
                showAlert("Успех", String.format("Баланс успешно пополнен на $%.2f", amount));
                updateUI();
                amountField.clear();
            } else {
                showAlert("Ошибка", "Не удалось пополнить баланс");
                // Откатываем изменения
                currentUser.withdraw(amount);
            }

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректную сумму");
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showMainScene(currentUser);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}