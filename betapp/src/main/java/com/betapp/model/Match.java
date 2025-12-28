package com.betapp.model;

public class Match {
    private int id;
    private String team1;
    private String team2;
    private double odds1;
    private double odds2;
    private double oddsDraw;
    private String status;
    private String result;
    private String dateTime;

    public Match(int id, String team1, String team2, double odds1, double odds2,
                 double oddsDraw, String status, String result, String dateTime) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.odds1 = odds1;
        this.odds2 = odds2;
        this.oddsDraw = oddsDraw;
        this.status = status;
        this.result = result;
        this.dateTime = dateTime;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTeam1() { return team1; }
    public void setTeam1(String team1) { this.team1 = team1; }

    public String getTeam2() { return team2; }
    public void setTeam2(String team2) { this.team2 = team2; }

    public double getOdds1() { return odds1; }
    public void setOdds1(double odds1) { this.odds1 = odds1; }

    public double getOdds2() { return odds2; }
    public void setOdds2(double odds2) { this.odds2 = odds2; }

    public double getOddsDraw() { return oddsDraw; }
    public void setOddsDraw(double oddsDraw) { this.oddsDraw = oddsDraw; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getMatchTitle() {
        return team1 + " vs " + team2;
    }

    public double getOddsForChoice(String choice) {
        switch (choice) {
            case "team1": return odds1;
            case "team2": return odds2;
            case "draw": return oddsDraw;
            default: return 1.0;
        }
    }

    // Метод для симуляции случайного результата
    public String simulateRandomResult() {
        double random = Math.random();
        double total = odds1 + oddsDraw + odds2;
        double team1Chance = odds1 / total;
        double drawChance = oddsDraw / total;

        if (random < team1Chance) {
            return "team1";
        } else if (random < team1Chance + drawChance) {
            return "draw";
        } else {
            return "team2";
        }
    }

    public boolean isBetWon(String betType) {
        if (result == null || result.isEmpty()) {
            return false;
        }
        return result.equals(betType);
    }
}