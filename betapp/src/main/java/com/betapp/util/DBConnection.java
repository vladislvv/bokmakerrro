package com.betapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:betapp.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке драйвера SQLite:");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void checkTables() {
        try (Connection conn = getConnection()) {
            System.out.println("=== Проверка структуры БД ===");

            var stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");

            String checkSql = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
            try (var rs = stmt.executeQuery(checkSql)) {
                System.out.println("Таблицы в базе данных:");
                while (rs.next()) {
                    String tableName = rs.getString("name");
                    System.out.println("- " + tableName);

                    checkTableColumns(conn, tableName);
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке таблиц:");
            e.printStackTrace();
        }
    }

    private static void checkTableColumns(Connection conn, String tableName) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            System.out.println("  Колонки таблицы '" + tableName + "':");
            while (rs.next()) {
                String columnName = rs.getString("name");
                String columnType = rs.getString("type");
                System.out.println("    - " + columnName + " (" + columnType + ")");
            }
        }
    }
}