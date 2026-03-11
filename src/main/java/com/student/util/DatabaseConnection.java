package com.student.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Database Connection Manager.
 * Loads credentials from .env file.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String ENV_FILE = ".env";

    private DatabaseConnection() {
        // private constructor for singleton
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    private void connect() throws SQLException {
        Map<String, String> env = loadEnv();

        String host     = env.getOrDefault("DB_HOST", "localhost");
        String port     = env.getOrDefault("DB_PORT", "5432");
        String dbName   = env.getOrDefault("DB_NAME", "student_system");
        String user     = env.getOrDefault("DB_USER", "postgres");
        String password = env.getOrDefault("DB_PASSWORD", "");

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found. Add postgresql jar to classpath.", e);
        }

        connection = DriverManager.getConnection(url, user, password);
        System.out.println(ConsoleColors.GREEN + "[DB] Connected to PostgreSQL: " + dbName + ConsoleColors.RESET);
    }

    private Map<String, String> loadEnv() {
        Map<String, String> envMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ENV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    envMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println(ConsoleColors.YELLOW + "[WARN] .env file not found. Using system environment variables." + ConsoleColors.RESET);
            // Fallback to system env vars
            String[] keys = {"DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_PASSWORD"};
            for (String key : keys) {
                String val = System.getenv(key);
                if (val != null) envMap.put(key, val);
            }
        }
        return envMap;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(ConsoleColors.CYAN + "[DB] Connection closed." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
