package com.amazonscale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AmazonScaleBackendApplicationTests {

    @Test
    void mainApplicationClassCanBeInstantiated() {
        AmazonScaleBackendApplication application = new AmazonScaleBackendApplication();
        assertNotNull(application);
    }
}
