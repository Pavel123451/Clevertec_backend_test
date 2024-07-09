package ru.clevertec.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolManager {
    private final List<Connection> connectionPool = new ArrayList<>();
    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    public ConnectionPoolManager(String url, String username, String password, int poolSize) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.poolSize = poolSize;
        initializeConnectionPool();
    }

    private void initializeConnectionPool() {
        try {
            for (int i = 0; i < poolSize; i++) {
                connectionPool.add(createNewConnectionForPool());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing connection pool", e);
        }
    }

    private Connection createNewConnectionForPool() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public synchronized Connection getConnection() {
        if (connectionPool.isEmpty()) {
            throw new RuntimeException("No available connection in the pool");
        }
        return connectionPool.removeLast();
    }

    public synchronized void releaseConnection(Connection connection) {
        connectionPool.add(connection);
    }

    public synchronized void closeAllConnections() {
        for (Connection connection : connectionPool) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

