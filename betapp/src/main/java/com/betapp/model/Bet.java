package com.betapp.model;

import java.time.LocalDateTime;

public class Bet {
    private int id;
    private int userId;
    private int matchId;
    private String betType; // "team1", "team2", "draw"
    private double amount;
    private double odds;
    private String status; // "pending", "won", "lost"
    private LocalDateTime placedAt;

    private String matchTitle;
    private String selectedTeam;

    public Bet(int id, int userId, int matchId, String betType, double amount,
               double odds, String status, LocalDateTime placedAt) {
        this.id = id;
        this.userId = userId;
        this.matchId = matchId;
        this.betType = betType;
        this.amount = amount;
        this.odds = odds;
        this.status = status;
        this.placedAt = placedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public String getBetType() { return betType; }
    public void setBetType(String betType) { this.betType = betType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getOdds() { return odds; }
    public void setOdds(double odds) { this.odds = odds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }

    public String getMatchTitle() { return matchTitle; }
    public void setMatchTitle(String matchTitle) { this.matchTitle = matchTitle; }

    public String getSelectedTeam() { return selectedTeam; }
    public void setSelectedTeam(String selectedTeam) { this.selectedTeam = selectedTeam; }

    public double getPotentialWin() {
        return amount * odds;
    }

    public double getActualWin() {
        if ("won".equals(status)) {
            return amount * odds;
        }
        return 0;
    }

    public String getBetTypeText() {
        switch (betType) {
            case "team1": return "П1";
            case "team2": return "П2";
            case "draw": return "X";
            default: return betType;
        }
    }
}