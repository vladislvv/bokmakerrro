package com.betapp.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        System.out.println("=== Начало инициализации базы данных ===");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Включаем поддержку внешних ключей
            stmt.execute("PRAGMA foreign_keys = ON");

            // Создаем таблицы
            createTables(stmt);

            // Очищаем старые данные
            clearOldData(stmt);

            // Заполняем тестовыми данными
            insertTestData(stmt);

            // Проверяем, что данные вставлены
            checkData(stmt);

            System.out.println("=== Инициализация базы данных завершена успешно ===");

        } catch (Exception e) {
            System.err.println("Ошибка при инициализации БД:");
            e.printStackTrace();
        }
    }

    private static void createTables(Statement stmt) throws Exception {
        System.out.println("=== Создание таблиц ===");

        // Таблица пользователей
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                balance REAL DEFAULT 0.0
            )
        """;
        stmt.execute(createUsersTable);
        System.out.println("✓ Таблица 'users' создана");

        // Таблица матчей
        String createMatchesTable = """
            CREATE TABLE IF NOT EXISTS matches (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                team1 TEXT NOT NULL,
                team2 TEXT NOT NULL,
                odds1 REAL NOT NULL,
                odds2 REAL NOT NULL,
                odds_draw REAL NOT NULL,
                status TEXT DEFAULT 'upcoming',
                result TEXT,
                datetime TEXT NOT NULL
            )
        """;
        stmt.execute(createMatchesTable);
        System.out.println("✓ Таблица 'matches' создана");

        // Таблица ставок
        String createBetsTable = """
            CREATE TABLE IF NOT EXISTS bets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                match_id INTEGER NOT NULL,
                bet_type TEXT NOT NULL,
                amount REAL NOT NULL,
                odds REAL NOT NULL,
                status TEXT DEFAULT 'pending',
                placed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (match_id) REFERENCES matches(id)
            )
        """;
        stmt.execute(createBetsTable);
        System.out.println("✓ Таблица 'bets' создана");
    }

    private static void clearOldData(Statement stmt) throws Exception {
        System.out.println("=== Очистка старых данных ===");

        // Отключаем внешние ключи для очистки
        stmt.execute("PRAGMA foreign_keys = OFF");

        // Очищаем в правильном порядке из-за внешних ключей
        stmt.execute("DELETE FROM bets");
        stmt.execute("DELETE FROM matches");
        stmt.execute("DELETE FROM users");

        // Сбрасываем автоинкремент
        stmt.execute("DELETE FROM sqlite_sequence WHERE name IN ('users', 'matches', 'bets')");

        // Включаем обратно внешние ключи
        stmt.execute("PRAGMA foreign_keys = ON");

        System.out.println("✓ Старые данные очищены");
    }

    private static void insertTestData(Statement stmt) throws Exception {
        System.out.println("=== Вставка тестовых данных ===");

        // Тестовые пользователи
        String insertUsers = """
            INSERT INTO users (username, password, balance) VALUES 
            ('user1', 'pass1', 1000),
            ('user2', 'pass2', 500),
            ('admin', 'admin', 5000)
        """;
        stmt.execute(insertUsers);
        System.out.println("✓ Тестовые пользователи добавлены");

        // Тестовые матчи (только предстоящие - 8 колонок)
        String insertUpcomingMatches = """
            INSERT INTO matches (team1, team2, odds1, odds2, odds_draw, datetime, status) VALUES 
            ('Реал Мадрид', 'Манчестер Сити', 2.45, 2.90, 3.40, '2024-12-25 21:00', 'upcoming'),
            ('Бавария Мюнхен', 'ПСЖ', 2.10, 3.20, 3.50, '2024-12-26 20:45', 'upcoming'),
            ('Ливерпуль', 'Милан', 1.85, 3.80, 4.00, '2024-12-27 19:30', 'upcoming'),
            ('Арсенал', 'Челси', 2.20, 3.10, 3.30, '2024-12-28 18:00', 'upcoming'),
            ('Манчестер Юнайтед', 'Тоттенхэм', 2.60, 2.80, 3.10, '2024-12-29 17:30', 'upcoming'),
            ('Лос-Анджелес Лейкерс', 'Бостон Селтикс', 1.95, 1.85, 0, '2024-12-30 03:00', 'upcoming'),
            ('Голден Стэйт Уорриорз', 'Чикаго Буллз', 1.65, 2.20, 0, '2024-12-31 04:30', 'upcoming'),
            ('Новак Джокович', 'Карлос Алькарас', 1.70, 2.15, 0, '2025-01-01 15:00', 'upcoming'),
            ('Даниил Медведев', 'Янник Зиннер', 2.05, 1.80, 0, '2025-01-02 16:30', 'upcoming'),
            ('Тампа Бэй Лайтнинг', 'Колорадо Эвеланш', 2.30, 1.70, 0, '2025-01-03 02:00', 'upcoming'),
            ('Торонто Мейпл Лифс', 'Нью-Йорк Рейнджерс', 1.90, 1.90, 0, '2025-01-04 01:30', 'upcoming'),
            ('Андердог', 'Фаворит', 5.50, 1.25, 3.80, '2025-01-05 20:00', 'upcoming'),
            ('Местная команда', 'Гость', 3.20, 2.10, 2.90, '2025-01-06 19:00', 'upcoming')
        """;
        stmt.execute(insertUpcomingMatches);
        System.out.println("✓ Предстоящие матчи добавлены");

        // Завершенные матчи (9 колонок, включая result)
        String insertFinishedMatches = """
            INSERT INTO matches (team1, team2, odds1, odds2, odds_draw, datetime, status, result) VALUES 
            ('Барселона', 'Реал Мадрид', 2.10, 3.20, 3.50, '2024-12-15 20:00', 'finished', 'team1'),
            ('Боруссия Дортмунд', 'Бавария', 3.80, 1.90, 3.40, '2024-12-14 19:30', 'finished', 'team2'),
            ('Интер', 'Милан', 2.50, 2.70, 3.20, '2024-12-13 20:45', 'finished', 'draw')
        """;
        stmt.execute(insertFinishedMatches);
        System.out.println("✓ Завершенные матчи добавлены");

        // Тестовые ставки - исправляем ID матчей
        String insertBets = """
            INSERT INTO bets (user_id, match_id, bet_type, amount, odds, status, placed_at) VALUES 
            (1, 1, 'team1', 50.00, 2.45, 'pending', datetime('now', '-2 days')),
            (1, 2, 'draw', 25.00, 3.50, 'pending', datetime('now', '-1 day')),
            (1, 4, 'team2', 100.00, 3.10, 'pending', datetime('now', '-3 hours')),
            (1, 14, 'team1', 10.00, 5.50, 'won', datetime('now', '-5 days')),
            (1, 15, 'team2', 75.00, 2.10, 'lost', datetime('now', '-7 days'))
        """;
        stmt.execute(insertBets);
        System.out.println("✓ Тестовые ставки добавлены");
    }

    private static void checkData(Statement stmt) throws Exception {
        System.out.println("=== Проверка данных ===");

        // Проверяем пользователей
        ResultSet usersRs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
        if (usersRs.next()) {
            System.out.println("✓ Пользователей: " + usersRs.getInt("count"));
        }

        // Проверяем матчи
        ResultSet matchesRs = stmt.executeQuery("SELECT COUNT(*) as count FROM matches");
        if (matchesRs.next()) {
            System.out.println("✓ Матчей: " + matchesRs.getInt("count"));
        }

        // Проверяем ставки
        ResultSet betsRs = stmt.executeQuery("SELECT COUNT(*) as count FROM bets");
        if (betsRs.next()) {
            System.out.println("✓ Ставок: " + betsRs.getInt("count"));
        }

        // Показываем список матчей с ID
        System.out.println("\nСписок матчей с ID:");
        ResultSet allMatches = stmt.executeQuery("SELECT id, team1, team2, status FROM matches ORDER BY id");
        while (allMatches.next()) {
            System.out.println("  ID " + allMatches.getInt("id") + ": " +
                    allMatches.getString("team1") + " vs " +
                    allMatches.getString("team2") + " [" +
                    allMatches.getString("status") + "]");
        }

        // Показываем ставки
        System.out.println("\nСписок ставок:");
        ResultSet allBets = stmt.executeQuery("""
            SELECT b.id, b.user_id, b.match_id, b.amount, b.odds, b.status, 
                   m.team1, m.team2 
            FROM bets b 
            JOIN matches m ON b.match_id = m.id
            ORDER BY b.id
        """);
        while (allBets.next()) {
            System.out.println("  Ставка #" + allBets.getInt("id") +
                    " на матч ID " + allBets.getInt("match_id") +
                    " (" + allBets.getString("team1") + " vs " +
                    allBets.getString("team2") + ") сумма: $" +
                    allBets.getDouble("amount"));
        }
    }
}