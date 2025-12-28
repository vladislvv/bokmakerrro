package com.betapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ManualDBCreator {

    public static void main(String[] args) {
        System.out.println("Ручное создание базы данных...");

        try {
            // Создаем подключение
            Connection conn = DriverManager.getConnection("jdbc:sqlite:betapp.db");
            Statement stmt = conn.createStatement();

            // Включаем внешние ключи
            stmt.execute("PRAGMA foreign_keys = ON");

            // Удаляем старые таблицы (если есть)
            try { stmt.execute("DROP TABLE IF EXISTS bets"); } catch (Exception e) {}
            try { stmt.execute("DROP TABLE IF EXISTS matches"); } catch (Exception e) {}
            try { stmt.execute("DROP TABLE IF EXISTS users"); } catch (Exception e) {}

            // Создаем таблицы
            String[] createTables = {
                    // Пользователи
                    "CREATE TABLE users (" +
                            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  username TEXT UNIQUE NOT NULL," +
                            "  password TEXT NOT NULL," +
                            "  balance REAL DEFAULT 0.0" +
                            ")",

                    // Матчи
                    "CREATE TABLE matches (" +
                            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  team1 TEXT NOT NULL," +
                            "  team2 TEXT NOT NULL," +
                            "  odds1 REAL NOT NULL," +
                            "  odds2 REAL NOT NULL," +
                            "  odds_draw REAL NOT NULL," +
                            "  status TEXT DEFAULT 'upcoming'," +
                            "  result TEXT," +
                            "  datetime TEXT NOT NULL" +
                            ")",

                    // Ставки
                    "CREATE TABLE bets (" +
                            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  user_id INTEGER NOT NULL," +
                            "  match_id INTEGER NOT NULL," +
                            "  bet_type TEXT NOT NULL," +
                            "  amount REAL NOT NULL," +
                            "  odds REAL NOT NULL," +
                            "  status TEXT DEFAULT 'pending'," +
                            "  placed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "  FOREIGN KEY (user_id) REFERENCES users(id)," +
                            "  FOREIGN KEY (match_id) REFERENCES matches(id)" +
                            ")"
            };

            for (String sql : createTables) {
                stmt.execute(sql);
                System.out.println("Таблица создана: " + sql.substring(0, 50) + "...");
            }

            // Добавляем тестовые данные
            String[] insertData = {
                    // Пользователи
                    "INSERT INTO users (username, password, balance) VALUES " +
                            "('user1', 'pass1', 1000)," +
                            "('user2', 'pass2', 500)," +
                            "('admin', 'admin', 5000)",

                    // Матчи
                    "INSERT INTO matches (team1, team2, odds1, odds2, odds_draw, datetime, status) VALUES " +
                            "('Реал Мадрид', 'Манчестер Сити', 2.45, 2.90, 3.40, '2024-12-25 21:00', 'upcoming')," +
                            "('Бавария Мюнхен', 'ПСЖ', 2.10, 3.20, 3.50, '2024-12-26 20:45', 'upcoming')"
            };

            for (String sql : insertData) {
                stmt.execute(sql);
                System.out.println("Данные добавлены: " + sql.substring(0, 50) + "...");
            }

            stmt.close();
            conn.close();

            System.out.println("База данных успешно создана вручную!");

        } catch (Exception e) {
            System.err.println("Ошибка при ручном создании БД:");
            e.printStackTrace();
        }
    }
}