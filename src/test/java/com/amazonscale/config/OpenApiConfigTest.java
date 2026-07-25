package com.amazonscale.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void testAmazonScaleOpenAPIBean() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.amazonScaleOpenAPI();

        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("AmazonScale Backend API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("v1.0");
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Amit Kumar Gupta");
    }
}
