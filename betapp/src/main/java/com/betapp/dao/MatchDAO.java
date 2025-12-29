package com.betapp.dao;

import com.betapp.model.Match;
import com.betapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    public List<Match> getAllMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches ORDER BY datetime DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                matches.add(createMatchFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matches;
    }

    public List<Match> getUpcomingMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE status = 'upcoming' ORDER BY datetime";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                matches.add(createMatchFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matches;
    }

    public Match getMatchById(int id) {
        String sql = "SELECT * FROM matches WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return createMatchFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Симулируем результат матча
    public boolean simulateMatchResult(int matchId) {
        Match match = getMatchById(matchId);
        if (match == null || !"upcoming".equals(match.getStatus())) {
            return false;
        }

        String result = match.simulateRandomResult();

        String sql = "UPDATE matches SET status = 'finished', result = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, result);
            pstmt.setInt(2, matchId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Результат матча ID " + matchId + " симулирован: " + result);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Определяем результаты всех ставок на матч
    public boolean determineBetResults(int matchId) {
        String result = getMatchResult(matchId);
        if (result == null) {
            return false;
        }

        String sql = "UPDATE bets SET status = " +
                "CASE WHEN bet_type = ? THEN 'won' ELSE 'lost' END " +
                "WHERE match_id = ? AND status = 'pending'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, result);
            pstmt.setInt(2, matchId);

            int rows = pstmt.executeUpdate();
            System.out.println("Обновлено " + rows + " ставок для матча ID " + matchId);
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Получаем результат матча
    public String getMatchResult(int matchId) {
        String sql = "SELECT result FROM matches WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, matchId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("result");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Match> getSimulatableMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE status = 'upcoming' ORDER BY datetime";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                matches.add(createMatchFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matches;
    }

    private Match createMatchFromResultSet(ResultSet rs) throws SQLException {
        return new Match(
                rs.getInt("id"),
                rs.getString("team1"),
                rs.getString("team2"),
                rs.getDouble("odds1"),
                rs.getDouble("odds2"),
                rs.getDouble("odds_draw"),
                rs.getString("status"),
                rs.getString("result"),
                rs.getString("datetime")
        );
    }
}