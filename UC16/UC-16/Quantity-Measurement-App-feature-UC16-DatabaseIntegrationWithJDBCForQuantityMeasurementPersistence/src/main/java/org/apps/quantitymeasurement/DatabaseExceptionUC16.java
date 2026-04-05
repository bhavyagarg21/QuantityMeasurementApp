package org.apps.quantitymeasurement;

public class DatabaseExceptionUC16 extends RuntimeException {
    public DatabaseExceptionUC16(String message) {
        super(message);
    }
    public DatabaseExceptionUC16(String message, Throwable cause) {
        super(message, cause);
    }
}