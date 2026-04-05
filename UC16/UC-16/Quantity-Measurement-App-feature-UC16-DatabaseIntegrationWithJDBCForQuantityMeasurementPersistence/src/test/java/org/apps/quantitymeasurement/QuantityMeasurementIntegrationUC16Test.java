package org.apps.quantitymeasurement;

//import org.apps.quantitymeasurement.QuantityMeasurementControllerUC16;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class QuantityMeasurementIntegrationUC16Test {

    private QuantityMeasurementControllerUC16 controller;

    @Before
    public void setup() {
        controller = new QuantityMeasurementControllerUC16();
    }

    @Test
    public void testGetPoolStatistics() {
        String stats = controller.getPoolStats();

        Assert.assertNotNull(stats);
        Assert.assertTrue(stats.contains("Available"));
        Assert.assertTrue(stats.contains("Used"));
        Assert.assertTrue(stats.contains("Total"));

        System.out.println(stats);
    }
}