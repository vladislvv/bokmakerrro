package com.betapp.controller;

import com.betapp.dao.BetDAO;
import com.betapp.dao.MatchDAO;
import com.betapp.model.Match;
import com.betapp.model.User;
import com.betapp.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class SimulateResultsController {

    @FXML private TableView<Match> matchesTable;
    @FXML private TableColumn<Match, String> matchColumn;
    @FXML private TableColumn<Match, String> dateColumn;
    @FXML private TableColumn<Match, String> oddsColumn;

    private User currentUser;
    private MatchDAO matchDAO = new MatchDAO();
    private BetDAO betDAO = new BetDAO();
    private ObservableList<Match> matchesList = FXCollections.observableArrayList();

    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
        }
    }

    @FXML
    public void initialize() {
        System.out.println("Инициализация SimulateResultsController...");
        setupTableColumns();
        loadMatches();
    }

    private void setupTableColumns() {
        matchColumn.setCellValueFactory(new PropertyValueFactory<>("matchTitle"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        oddsColumn.setCellFactory(column -> new TableCell<Match, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Match match = getTableView().getItems().get(getIndex());
                    setText(String.format("П1: %.2f | X: %.2f | П2: %.2f",
                            match.getOdds1(), match.getOddsDraw(), match.getOdds2()));
                }
            }
        });
    }

    private void loadMatches() {
        List<Match> matches = matchDAO.getSimulatableMatches();
        matchesList.clear();
        matchesList.addAll(matches);
        matchesTable.setItems(matchesList);
        System.out.println("Загружено матчей для симуляции: " + matches.size());
    }

    @FXML
    public void simulateSelected() {
        Match selected = matchesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите матч для симуляции", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Симуляция результата");
        confirmAlert.setHeaderText("Подтвердите симуляцию");
        confirmAlert.setContentText(String.format(
                "Матч: %s\n\n" +
                        "Вы уверены, что хотите симулировать результат этого матча?\n" +
                        "Это определит все ставки и выплатит выигрыши.",
                selected.getMatchTitle()
        ));

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = betDAO.processMatchComplete(selected.getId());

            if (success) {
                String result = matchDAO.getMatchResult(selected.getId());
                showAlert("Успех", String.format(
                        "Результат матча симулирован!\n\n" +
                                "Матч: %s\n" +
                                "Результат: %s\n\n" +
                                "Все ставки определены, выигрыши выплачены.",
                        selected.getMatchTitle(),
                        getResultText(selected, result)
                ), Alert.AlertType.INFORMATION);

                loadMatches(); // Обновляем список
            } else {
                showAlert("Ошибка", "Не удалось симулировать результат", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void simulateAll() {
        if (matchesList.isEmpty()) {
            showAlert("Информация", "Нет матчей для симуляции", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Симуляция всех матчей");
        confirmAlert.setHeaderText("Подтвердите симуляцию");
        confirmAlert.setContentText(String.format(
                "Вы уверены, что хотите симулировать результаты %d матчей?\n" +
                        "Это определит все ставки и выплатит выигрыши.",
                matchesList.size()
        ));

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            int count = 0;
            for (Match match : matchesList) {
                if (betDAO.processMatchComplete(match.getId())) {
                    count++;
                }
            }

            showAlert("Успех", String.format(
                    "Симуляция завершена!\n\n" +
                            "Обработано матчей: %d\n" +
                            "Все ставки определены, выигрыши выплачены.",
                    count
            ), Alert.AlertType.INFORMATION);

            loadMatches(); // Обновляем список
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showMainScene(currentUser);
    }

    private String getResultText(Match match, String result) {
        if (result == null) return "Не определен";
        switch (result) {
            case "team1": return match.getTeam1() + " (П1)";
            case "team2": return match.getTeam2() + " (П2)";
            case "draw": return "Ничья (X)";
            default: return result;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}