package com.betapp.controller;

import com.betapp.dao.BetDAO;
import com.betapp.dao.MatchDAO;
import com.betapp.dao.UserDAO;
import com.betapp.model.Match;
import com.betapp.model.User;
import com.betapp.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MakeBetController {

    @FXML private TableView<Match> matchesTable;
    @FXML private TableColumn<Match, String> matchColumn;
    @FXML private TableColumn<Match, String> dateColumn;
    @FXML private TableColumn<Match, Double> odds1Column;
    @FXML private TableColumn<Match, Double> oddsDrawColumn;
    @FXML private TableColumn<Match, Double> odds2Column;

    @FXML private Label selectedMatchLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField amountField;
    @FXML private ToggleGroup betTypeGroup;
    @FXML private RadioButton team1Radio;
    @FXML private RadioButton drawRadio;
    @FXML private RadioButton team2Radio;
    @FXML private Label potentialWinLabel;
    @FXML private Label oddsLabel;
    @FXML private CheckBox instantResultCheckbox;

    private User currentUser;
    private Match selectedMatch;  // Вот здесь объявляем переменную
    private MatchDAO matchDAO = new MatchDAO();
    private BetDAO betDAO = new BetDAO();
    private UserDAO userDAO = new UserDAO();
    private ObservableList<Match> matchesList = FXCollections.observableArrayList();

    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            updateUI();
            loadMatches();
        } else {
            System.err.println("Ошибка: передан не User объект в MakeBetController");
        }
    }

    @FXML
    public void initialize() {
        System.out.println("Инициализация MakeBetController...");

        // Инициализируем ToggleGroup если он не связан через FXML
        if (betTypeGroup == null) {
            betTypeGroup = new ToggleGroup();
            if (team1Radio != null) team1Radio.setToggleGroup(betTypeGroup);
            if (drawRadio != null) drawRadio.setToggleGroup(betTypeGroup);
            if (team2Radio != null) team2Radio.setToggleGroup(betTypeGroup);
            System.out.println("ToggleGroup создан вручную");
        }

        setupTableColumns();
        setupListeners();
    }

    private void setupTableColumns() {
        matchColumn.setCellValueFactory(new PropertyValueFactory<>("matchTitle"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        odds1Column.setCellValueFactory(new PropertyValueFactory<>("odds1"));
        oddsDrawColumn.setCellValueFactory(new PropertyValueFactory<>("oddsDraw"));
        odds2Column.setCellValueFactory(new PropertyValueFactory<>("odds2"));

        // Форматирование колонок
        odds1Column.setCellFactory(column -> formatOddsCell("team1"));
        oddsDrawColumn.setCellFactory(column -> formatOddsCell("draw"));
        odds2Column.setCellFactory(column -> formatOddsCell("team2"));
    }

    private TableCell<Match, Double> formatOddsCell(String betType) {
        return new TableCell<Match, Double>() {
            @Override
            protected void updateItem(Double odds, boolean empty) {
                super.updateItem(odds, empty);
                if (empty || odds == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", odds));
                    setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-fill: #ffffff;");

                    // Подсветка лучшего коэффициента
                    Match match = getTableView().getItems().get(getIndex());
                    if (isBestOdds(match, betType, odds)) {
                        setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
                    }
                }
            }
        };
    }

    private boolean isBestOdds(Match match, String betType, double currentOdds) {
        double odds1 = match.getOdds1();
        double oddsDraw = match.getOddsDraw();
        double odds2 = match.getOdds2();

        switch (betType) {
            case "team1":
                return currentOdds == odds1 && odds1 >= oddsDraw && odds1 >= odds2;
            case "draw":
                return currentOdds == oddsDraw && oddsDraw >= odds1 && oddsDraw >= odds2;
            case "team2":
                return currentOdds == odds2 && odds2 >= odds1 && odds2 >= oddsDraw;
            default:
                return false;
        }
    }

    private void setupListeners() {
        matchesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectMatch(newValue);
                    }
                }
        );

        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            calculatePotentialWin();
        });

        if (betTypeGroup != null) {
            betTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                calculatePotentialWin();
            });
        } else {
            System.err.println("betTypeGroup is null in setupListeners");
        }
    }

    private void updateUI() {
        if (currentUser != null) {
            balanceLabel.setText(String.format("Ваш баланс: $%.2f", currentUser.getBalance()));
        }
    }

    private void loadMatches() {
        List<Match> matches = matchDAO.getUpcomingMatches();
        matchesList.clear();
        matchesList.addAll(matches);
        matchesTable.setItems(matchesList);

        if (!matchesList.isEmpty()) {
            matchesTable.getSelectionModel().selectFirst();
            selectMatch(matchesList.get(0));
        }
    }

    private void selectMatch(Match match) {
        this.selectedMatch = match;  // Вот здесь инициализируем
        selectedMatchLabel.setText("Выбран матч: " + match.getMatchTitle());

        // Устанавливаем коэффициенты
        if (team1Radio != null) {
            team1Radio.setText(match.getTeam1() + " (коэф. " + String.format("%.2f", match.getOdds1()) + ")");
        }
        if (drawRadio != null) {
            drawRadio.setText("Ничья (коэф. " + String.format("%.2f", match.getOddsDraw()) + ")");
        }
        if (team2Radio != null) {
            team2Radio.setText(match.getTeam2() + " (коэф. " + String.format("%.2f", match.getOdds2()) + ")");
        }

        // Сбрасываем выбор типа ставки
        if (betTypeGroup != null && team1Radio != null) {
            betTypeGroup.selectToggle(team1Radio);
        }
        calculatePotentialWin();
    }

    private void calculatePotentialWin() {
        if (selectedMatch == null || amountField.getText().isEmpty()) {
            potentialWinLabel.setText("Потенциальный выигрыш: $0.00");
            oddsLabel.setText("Коэффициент: 0.00");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                potentialWinLabel.setText("Потенциальный выигрыш: $0.00");
                oddsLabel.setText("Коэффициент: 0.00");
                return;
            }

            String betType = getSelectedBetType();
            double odds = selectedMatch.getOddsForChoice(betType);

            oddsLabel.setText("Коэффициент: " + String.format("%.2f", odds));
            double potentialWin = amount * odds;
            potentialWinLabel.setText(String.format("Потенциальный выигрыш: $%.2f", potentialWin));

        } catch (NumberFormatException e) {
            potentialWinLabel.setText("Потенциальный выигрыш: $0.00");
            oddsLabel.setText("Коэффициент: 0.00");
        }
    }

    private String getSelectedBetType() {
        if (betTypeGroup == null || betTypeGroup.getSelectedToggle() == null) {
            return "team1"; // значение по умолчанию
        }

        RadioButton selected = (RadioButton) betTypeGroup.getSelectedToggle();
        if (selected == team1Radio) return "team1";
        if (selected == drawRadio) return "draw";
        if (selected == team2Radio) return "team2";
        return "team1"; // значение по умолчанию
    }

    @FXML
    public void placeBet() {
        if (selectedMatch == null) {
            showAlert("Ошибка", "Выберите матч для ставки", Alert.AlertType.ERROR);
            return;
        }

        if (amountField.getText().isEmpty()) {
            showAlert("Ошибка", "Введите сумму ставки", Alert.AlertType.ERROR);
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());

            if (amount <= 0) {
                showAlert("Ошибка", "Сумма ставки должна быть больше 0", Alert.AlertType.ERROR);
                return;
            }

            if (amount > currentUser.getBalance()) {
                showAlert("Ошибка", "Недостаточно средств на балансе", Alert.AlertType.ERROR);
                return;
            }

            String betType = getSelectedBetType();
            double odds = selectedMatch.getOddsForChoice(betType);

            // Подтверждение ставки
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение ставки");
            confirmAlert.setHeaderText("Подтвердите ставку");
            confirmAlert.setContentText(String.format(
                    "Матч: %s\nСтавка: %s\nСумма: $%.2f\nКоэффициент: %.2f\nПотенциальный выигрыш: $%.2f%s",
                    selectedMatch.getMatchTitle(),
                    getBetTypeText(betType),
                    amount,
                    odds,
                    amount * odds,
                    instantResultCheckbox.isSelected() ? "\n\n⚠️ Результат будет определен сразу!" : ""
            ));

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            // Списание средств
            if (!currentUser.withdraw(amount)) {
                showAlert("Ошибка", "Недостаточно средств на балансе", Alert.AlertType.ERROR);
                return;
            }

            // Сохранение ставки в базе
            boolean betPlaced = betDAO.placeBet(
                    currentUser.getId(),
                    selectedMatch.getId(),
                    betType,
                    amount,
                    odds
            );

            if (betPlaced) {
                // Обновляем баланс в базе
                userDAO.updateBalance(currentUser.getId(), currentUser.getBalance());

                // Если выбрана мгновенная проверка
                if (instantResultCheckbox.isSelected()) {
                    processInstantResult(selectedMatch.getId(), betType, amount, odds);
                } else {
                    // Обычная ставка (ожидание)
                    showAlert("Успех", "Ставка успешно размещена! Результат будет позже.", Alert.AlertType.INFORMATION);
                }

                // Обновляем UI
                updateUI();
                amountField.clear();

                // Возвращаемся на главную страницу
                SceneManager.showMainScene(currentUser);

            } else {
                // Возвращаем средства при ошибке
                currentUser.deposit(amount);
                showAlert("Ошибка", "Не удалось разместить ставку", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректную сумму", Alert.AlertType.ERROR);
        }
    }

    private void processInstantResult(int matchId, String betType, double amount, double odds) {
        BetDAO betDAO = new BetDAO();

        // Симулируем результат матча и определяем ставки
        boolean processed = betDAO.processMatchComplete(matchId);

        if (processed) {
            // Проверяем результат конкретной ставки
            String result = new MatchDAO().getMatchResult(matchId);

            if (result != null && result.equals(betType)) {
                // Ставка выиграла!
                double winAmount = amount * odds;
                currentUser.deposit(winAmount); // Добавляем выигрыш к балансу
                userDAO.updateBalance(currentUser.getId(), currentUser.getBalance());

                showAlert("ПОБЕДА! 🎉", String.format(
                        "Ваша ставка выиграла!\n\n" +
                                "Матч: %s\n" +
                                "Результат: %s\n" +
                                "Ваша ставка: %s\n" +
                                "Сумма ставки: $%.2f\n" +
                                "Коэффициент: %.2f\n" +
                                "ВЫИГРЫШ: $%.2f\n\n" +
                                "💰 Новый баланс: $%.2f",
                        selectedMatch.getMatchTitle(),
                        getResultText(result),
                        getBetTypeText(betType),
                        amount,
                        odds,
                        winAmount,
                        currentUser.getBalance()
                ), Alert.AlertType.INFORMATION);

            } else {
                // Ставка проиграла
                showAlert("Проигрыш 😢", String.format(
                        "Ваша ставка не сыграла.\n\n" +
                                "Матч: %s\n" +
                                "Результат: %s\n" +
                                "Ваша ставка: %s\n" +
                                "Сумма ставки: $%.2f\n\n" +
                                "Попробуйте ещё раз!",
                        selectedMatch.getMatchTitle(),
                        getResultText(result),
                        getBetTypeText(betType),
                        amount
                ), Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Ошибка", "Не удалось определить результат матча", Alert.AlertType.ERROR);
        }
    }

    private String getBetTypeText(String betType) {
        switch (betType) {
            case "team1": return selectedMatch.getTeam1() + " (П1)";
            case "team2": return selectedMatch.getTeam2() + " (П2)";
            case "draw": return "Ничья (X)";
            default: return betType;
        }
    }

    private String getResultText(String result) {
        if (result == null) return "Не определен";
        switch (result) {
            case "team1": return selectedMatch.getTeam1() + " (П1)";
            case "team2": return selectedMatch.getTeam2() + " (П2)";
            case "draw": return "Ничья (X)";
            default: return result;
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showMainScene(currentUser);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}