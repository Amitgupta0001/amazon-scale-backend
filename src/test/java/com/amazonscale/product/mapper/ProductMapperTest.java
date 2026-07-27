package com.amazonscale.product.mapper;

import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    @Test
    @DisplayName("Should correctly map ProductRequest DTO to Product entity")
    void shouldMapProductRequestToProductEntity() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Monitor");
        request.setDescription("4K Monitor");
        request.setImageUrl("https://example.com/monitor.jpg");
        request.setPrice(new BigDecimal("399.99"));
        request.setStock(15);
        request.setBrand("DisplayBrand");

        // Act
        Product product = ProductMapper.toEntity(request);

        // Assert
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("Monitor");
        assertThat(product.getDescription()).isEqualTo("4K Monitor");
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/monitor.jpg");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("399.99"));
        assertThat(product.getStock()).isEqualTo(15);
        assertThat(product.getBrand()).isEqualTo("DisplayBrand");
    }

    @Test
    @DisplayName("Should correctly map Product entity to ProductResponse DTO")
    void shouldMapProductEntityToProductResponseDto() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Monitor")
                .description("4K Monitor")
                .imageUrl("https://example.com/monitor.jpg")
                .price(new BigDecimal("399.99"))
                .stock(15)
                .brand("DisplayBrand")
                .active(true)
                .build();

        // Act
        ProductResponse response = ProductMapper.toResponse(product);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Monitor");
        assertThat(response.getDescription()).isEqualTo("4K Monitor");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("399.99"));
        assertThat(response.getStock()).isEqualTo(15);
        assertThat(response.getBrand()).isEqualTo("DisplayBrand");
        assertThat(response.getActive()).isTrue();
    }

    @Test
    @DisplayName("Should build ProductMapper using Builder pattern")
    void shouldBuildProductMapperUsingBuilder() {
        // Act
        ProductMapper mapper = ProductMapper.builder().build();

        // Assert
        assertThat(mapper).isNotNull();
    }
}