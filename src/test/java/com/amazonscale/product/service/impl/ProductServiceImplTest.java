package com.amazonscale.product.service.impl;

import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest request;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Tablet")
                .description("10-inch Tablet")
                .price(new BigDecimal("299.99"))
                .stock(20)
                .brand("TechBrand")
                .active(true)
                .build();

        request = new ProductRequest();
        request.setName("Tablet");
        request.setDescription("10-inch Tablet");
        request.setPrice(new BigDecimal("299.99"));
        request.setStock(20);
        request.setBrand("TechBrand");
    }

    @Test
    void createProductSuccess() {
        // Arrange
        when(repository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tablet", response.getName());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductSuccess() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.getProduct(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(99L));
    }

    @Test
    void getAllProductsSuccess() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(product));

        // Act
        List<ProductResponse> responses = productService.getAllProducts();

        // Assert
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
    }

    @Test
    void updateProductSuccess() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.updateProduct(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(repository, times(1)).save(product);
    }

    @Test
    void deleteProductSuccess() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(repository, times(1)).delete(product);
    }
}