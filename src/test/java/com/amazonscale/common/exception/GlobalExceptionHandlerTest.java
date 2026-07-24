package com.amazonscale.common.exception;

import com.amazonscale.category.exception.CategoryAlreadyExistsException;
import com.amazonscale.category.exception.CategoryNotFoundException;
import com.amazonscale.category.exception.InvalidCategoryHierarchyException;
import com.amazonscale.common.response.ErrorResponse;
import com.amazonscale.inventory.exception.InsufficientStockException;
import com.amazonscale.inventory.exception.InventoryAlreadyExistsException;
import com.amazonscale.inventory.exception.InventoryNotFoundException;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.user.exception.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    void handleProductNotFound() {
        // Arrange
        ProductNotFoundException ex = new ProductNotFoundException(1L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleProductNotFound(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleEmailAlreadyExists() {
        // Arrange
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("user@example.com");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmailAlreadyExists(ex, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleCategoryNotFound() {
        // Arrange
        CategoryNotFoundException ex = new CategoryNotFoundException(5L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryNotFound(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleCategoryAlreadyExists() {
        // Arrange
        CategoryAlreadyExistsException ex = new CategoryAlreadyExistsException("Electronics");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryAlreadyExists(ex, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleInvalidCategoryHierarchy() {
        // Arrange
        InvalidCategoryHierarchyException ex = new InvalidCategoryHierarchyException();

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryHierarchy(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleInventoryNotFound() {
        // Arrange
        InventoryNotFoundException ex = new InventoryNotFoundException(10L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInventoryNotFound(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleInventoryAlreadyExists() {
        // Arrange
        InventoryAlreadyExistsException ex = new InventoryAlreadyExistsException(12L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInventoryAlreadyExists(ex, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void handleInsufficientStock() {
        // Arrange
        InsufficientStockException ex = new InsufficientStockException("Insufficient stock");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInsufficientStock(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
