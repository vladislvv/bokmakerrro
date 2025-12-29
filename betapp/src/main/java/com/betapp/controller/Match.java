package com.betapp.controller;

import javafx.beans.property.*;

public class Match {

    private final int id;
    private final StringProperty homeTeam;
    private final StringProperty awayTeam;
    private final DoubleProperty homeOdds;
    private final DoubleProperty drawOdds;
    private final DoubleProperty awayOdds;

    public Match(int id, String homeTeam, String awayTeam, double homeOdds, double drawOdds, double awayOdds) {
        this.id = id;
        this.homeTeam = new SimpleStringProperty(homeTeam);
        this.awayTeam = new SimpleStringProperty(awayTeam);
        this.homeOdds = new SimpleDoubleProperty(homeOdds);
        this.drawOdds = new SimpleDoubleProperty(drawOdds);
        this.awayOdds = new SimpleDoubleProperty(awayOdds);
    }

    public int getId() { return id; }
    public StringProperty homeTeamProperty() { return homeTeam; }
    public StringProperty awayTeamProperty() { return awayTeam; }
    public DoubleProperty homeOddsProperty() { return homeOdds; }
    public DoubleProperty drawOddsProperty() { return drawOdds; }
    public DoubleProperty awayOddsProperty() { return awayOdds; }
}
