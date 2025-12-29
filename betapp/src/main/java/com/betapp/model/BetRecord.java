package com.betapp.model;

public class BetRecord {
    private String match;
    private String choice;
    private double amount;
    private String result;

    public BetRecord(String match, String choice, double amount, String result) {
        this.match = match;
        this.choice = choice;
        this.amount = amount;
        this.result = result;
    }

    public String getMatch() { return match; }
    public String getChoice() { return choice; }
    public double getAmount() { return amount; }
    public String getResult() { return result; }
}
