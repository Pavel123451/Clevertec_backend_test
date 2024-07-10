package ru.clevertec.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolManager {
    private final List<Connection> connectionPool = new ArrayList<>();
    private final DataSource dataSource;
    private final int poolSize;

    public ConnectionPoolManager(DataSource dataSource, int poolSize) {
        this.dataSource = dataSource;
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
        return dataSource.getConnection();
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

