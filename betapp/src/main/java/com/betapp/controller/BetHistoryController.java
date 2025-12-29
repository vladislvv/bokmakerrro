package com.betapp.controller;

import com.betapp.dao.BetDAO;
import com.betapp.model.Bet;
import com.betapp.model.User;
import com.betapp.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class BetHistoryController {

    @FXML private TableView<Bet> betsTable;
    @FXML private TableColumn<Bet, String> matchColumn;
    @FXML private TableColumn<Bet, String> betColumn;
    @FXML private TableColumn<Bet, Double> amountColumn;
    @FXML private TableColumn<Bet, Double> oddsColumn;
    @FXML private TableColumn<Bet, Double> winColumn;
    @FXML private TableColumn<Bet, String> statusColumn;
    @FXML private TableColumn<Bet, String> dateColumn;

    private User currentUser;
    private BetDAO betDAO = new BetDAO();
    private ObservableList<Bet> betsList = FXCollections.observableArrayList();

    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            loadBets();
        }
    }

    @FXML
    public void initialize() {
        setupTableColumns();
    }

    private void setupTableColumns() {
        matchColumn.setCellValueFactory(new PropertyValueFactory<>("matchTitle"));
        betColumn.setCellValueFactory(new PropertyValueFactory<>("selectedTeam"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        oddsColumn.setCellValueFactory(new PropertyValueFactory<>("odds"));
        winColumn.setCellValueFactory(new PropertyValueFactory<>("actualWin"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("placedAt"));

        // Форматирование колонок
        amountColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Bet, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });

        oddsColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Bet, Double>() {
            @Override
            protected void updateItem(Double odds, boolean empty) {
                super.updateItem(odds, empty);
                if (empty || odds == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", odds));
                }
            }
        });

        winColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Bet, Double>() {
            @Override
            protected void updateItem(Double win, boolean empty) {
                super.updateItem(win, empty);
                if (empty || win == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", win));
                }
            }
        });

        statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Bet, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(getStatusText(status));
                    setStyle(getStatusStyle(status));
                }
            }

            private String getStatusText(String status) {
                switch (status) {
                    case "pending": return "Ожидает";
                    case "won": return "Выиграна";
                    case "lost": return "Проиграна";
                    default: return status;
                }
            }

            private String getStatusStyle(String status) {
                switch (status) {
                    case "won": return "-fx-text-fill: green; -fx-font-weight: bold;";
                    case "lost": return "-fx-text-fill: red;";
                    default: return "-fx-text-fill: orange;";
                }
            }
        });
    }

    private void loadBets() {
        if (currentUser != null) {
            List<Bet> bets = betDAO.getUserBets(currentUser.getId());
            betsList.clear();
            betsList.addAll(bets);
            betsTable.setItems(betsList);
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showMainScene(currentUser);
    }
}