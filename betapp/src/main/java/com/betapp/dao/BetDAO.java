package com.betapp.dao;

import com.betapp.model.Bet;
import com.betapp.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BetDAO {

    public boolean placeBet(int userId, int matchId, String betType, double amount, double odds) {
        String sql = "INSERT INTO bets (user_id, match_id, bet_type, amount, odds, status, placed_at) VALUES (?, ?, ?, ?, ?, 'pending', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, matchId);
            pstmt.setString(3, betType);
            pstmt.setDouble(4, amount);
            pstmt.setDouble(5, odds);
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ВАЖНО: Этот метод должен БЫТЬ с параметром int userId
    public List<Bet> getUserBets(int userId) {
        List<Bet> bets = new ArrayList<>();
        String sql = "SELECT b.*, m.team1, m.team2, m.result " +
                "FROM bets b " +
                "JOIN matches m ON b.match_id = m.id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.placed_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Bet bet = new Bet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("match_id"),
                        rs.getString("bet_type"),
                        rs.getDouble("amount"),
                        rs.getDouble("odds"),
                        rs.getString("status"),
                        rs.getTimestamp("placed_at").toLocalDateTime()
                );

                // Добавляем дополнительную информацию для отображения
                String team1 = rs.getString("team1");
                String team2 = rs.getString("team2");
                bet.setMatchTitle(team1 + " vs " + team2);

                // Определяем название выбранной команды
                String betType = rs.getString("bet_type");
                String selectedTeam = "";
                switch (betType) {
                    case "team1":
                        selectedTeam = team1 + " (П1)";
                        break;
                    case "team2":
                        selectedTeam = team2 + " (П2)";
                        break;
                    case "draw":
                        selectedTeam = "Ничья (X)";
                        break;
                }
                bet.setSelectedTeam(selectedTeam);

                bets.add(bet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bets;
    }

    public boolean processWinningBets(int matchId) {
        // Находим все выигравшие ставки для этого матча
        String findBetsSql = """
            SELECT b.id, b.user_id, b.amount, b.odds 
            FROM bets b 
            WHERE b.match_id = ? AND b.status = 'won'
        """;

        String updateBalanceSql = "UPDATE users SET balance = balance + ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement findStmt = conn.prepareStatement(findBetsSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSql)) {

            findStmt.setInt(1, matchId);
            ResultSet rs = findStmt.executeQuery();

            int processedCount = 0;
            while (rs.next()) {
                int betId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                double amount = rs.getDouble("amount");
                double odds = rs.getDouble("odds");
                double winAmount = amount * odds;

                // Выплачиваем выигрыш
                updateStmt.setDouble(1, winAmount);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

                System.out.println("Выплачен выигрыш: пользователь " + userId +
                        ", ставка " + betId + ", сумма $" + winAmount);
                processedCount++;
            }

            System.out.println("Обработано " + processedCount + " выигравших ставок");
            return processedCount > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean processMatchComplete(int matchId) {
        MatchDAO matchDAO = new MatchDAO();

        // 1. Симулируем результат матча
        boolean simulated = matchDAO.simulateMatchResult(matchId);
        if (!simulated) {
            return false;
        }

        // 2. Определяем результаты ставок
        boolean betsDetermined = matchDAO.determineBetResults(matchId);

        // 3. Выплачиваем выигрыши
        boolean winningsPaid = processWinningBets(matchId);

        return betsDetermined && winningsPaid;
    }
}