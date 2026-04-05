package org.apps.quantitymeasurement;

import java.util.logging.Logger;

public class QuantityMeasurementDatabaseRepositoryUC16 {

    private static final Logger logger =
            Logger.getLogger(QuantityMeasurementDatabaseRepositoryUC16.class.getName());

    private static QuantityMeasurementDatabaseRepositoryUC16 instance;

    private ConnectionPoolUC16 connectionPool;

    private QuantityMeasurementDatabaseRepositoryUC16() {
        connectionPool = ConnectionPoolUC16.getInstance();
    }

    public static synchronized QuantityMeasurementDatabaseRepositoryUC16 getInstance() {
        if (instance == null) {
            instance = new QuantityMeasurementDatabaseRepositoryUC16();
        }
        return instance;
    }

    public String getPoolStatistics() {
        try {
            return connectionPool.getPoolStats();
        } catch (Exception e) {
            logger.severe("Error fetching pool stats: " + e.getMessage());
            throw new DatabaseExceptionUC16("Failed to fetch pool stats", e);
        }
    }
}