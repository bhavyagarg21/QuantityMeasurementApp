package org.apps.quantitymeasurement;

import java.sql.*;
import java.util.*;
public class ConnectionPoolUC16 {

    private static ConnectionPoolUC16 instance;

    private final Queue<Connection> availableConnections = new LinkedList<>();
    private final Queue<Connection> usedConnections = new LinkedList<>();

    private static final int INITIAL_POOL_SIZE = 3;

    private ConnectionPoolUC16() {
        try {
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                availableConnections.add(createConnection());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing connection pool", e);
        }
    }

    public static synchronized ConnectionPoolUC16 getInstance() {
        if (instance == null) {
            instance = new ConnectionPoolUC16();
        }
        return instance;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb",
                "root",
                "rootroot"
        );
    }

    public synchronized Connection getConnection() {
        if (availableConnections.isEmpty()) {
            throw new RuntimeException("No available connections");
        }
        Connection conn = availableConnections.poll();
        usedConnections.add(conn);
        return conn;
    }

    public synchronized void releaseConnection(Connection conn) {
        if (conn != null) {
            usedConnections.remove(conn);
            availableConnections.add(conn);
        }
    }

    public synchronized String getPoolStats() {
        return "Connection Pool Stats -> " +
                "Available: " + availableConnections.size() +
                ", Used: " + usedConnections.size() +
                ", Total: " + (availableConnections.size() + usedConnections.size());
    }
}
