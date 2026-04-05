package org.apps.quantitymeasurement;

public class QuantityMeasurementControllerUC16 {

    private final QuantityMeasurementDatabaseRepositoryUC16 repository;

    public QuantityMeasurementControllerUC16() {
        this.repository = QuantityMeasurementDatabaseRepositoryUC16.getInstance();
    }

    public String getPoolStats() {
        return repository.getPoolStatistics();
    }
}
