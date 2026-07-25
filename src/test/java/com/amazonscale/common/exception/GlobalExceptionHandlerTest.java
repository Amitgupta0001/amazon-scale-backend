package com.amazonscale.common.exception;

import com.amazonscale.cart.exception.CartItemNotFoundException;
import com.amazonscale.cart.exception.CartNotFoundException;
import com.amazonscale.cart.exception.InvalidQuantityException;
import com.amazonscale.category.exception.CategoryAlreadyExistsException;
import com.amazonscale.category.exception.CategoryNotFoundException;
import com.amazonscale.category.exception.InvalidCategoryHierarchyException;
import com.amazonscale.common.response.ErrorResponse;
import com.amazonscale.inventory.exception.InsufficientStockException;
import com.amazonscale.inventory.exception.InventoryAlreadyExistsException;
import com.amazonscale.inventory.exception.InventoryNotFoundException;
import com.amazonscale.order.exception.EmptyCartException;
import com.amazonscale.order.exception.InvalidOrderStatusTransitionException;
import com.amazonscale.order.exception.OrderNotFoundException;
import com.amazonscale.product.exception.ProductInactiveException;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.user.exception.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    private void assertErrorResponse(ResponseEntity<ErrorResponse> response, HttpStatus expectedStatus, String expectedMessage) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedStatus.value(), response.getBody().getStatus());
        assertEquals(expectedStatus.getReasonPhrase(), response.getBody().getError());
        assertEquals(expectedMessage, response.getBody().getMessage());
        assertEquals("/api/v1/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleProductNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException(1L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleProductNotFound(ex, request);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    void handleEmailAlreadyExists() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("user@example.com");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmailAlreadyExists(ex, request);
        assertErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage());
    }

    @Test
    void handleCategoryNotFound() {
        CategoryNotFoundException ex = new CategoryNotFoundException(5L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryNotFound(ex, request);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    void handleCategoryAlreadyExists() {
        CategoryAlreadyExistsException ex = new CategoryAlreadyExistsException("Electronics");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryAlreadyExists(ex, request);
        assertErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage());
    }

    @Test
    void handleInvalidCategoryHierarchy() {
        InvalidCategoryHierarchyException ex = new InvalidCategoryHierarchyException();
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryHierarchy(ex, request);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    void handleInventoryNotFound() {
        InventoryNotFoundException ex = new InventoryNotFoundException(10L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInventoryNotFound(ex, request);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    void handleInventoryAlreadyExists() {
        InventoryAlreadyExistsException ex = new InventoryAlreadyExistsException(12L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInventoryAlreadyExists(ex, request);
        assertErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage());
    }

    @Test
    void handleInsufficientStock() {
        InsufficientStockException ex = new InsufficientStockException("Insufficient stock");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInsufficientStock(ex, request);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    void handleCartNotFound() {
        CartNotFoundException ex = new CartNotFoundException(50L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCartNotFound(ex, request);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    void handleCartItemNotFound() {
        CartItemNotFoundException ex = new CartItemNotFoundException(20L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCartItemNotFound(ex, request);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    void handleInvalidQuantity() {
        InvalidQuantityException ex = new InvalidQuantityException("Quantity must be greater than 0");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidQuantity(ex, request);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    void handleOrderNotFound() {
        OrderNotFoundException ex = new OrderNotFoundException("Order not found with id: 100");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderNotFound(ex, request);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    void handleEmptyCart() {
        EmptyCartException ex = new EmptyCartException("Cannot place order with empty cart");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmptyCart(ex, request);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    void handleInvalidOrderStatusTransition() {
        InvalidOrderStatusTransitionException ex = new InvalidOrderStatusTransitionException("Invalid transition");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidOrderStatusTransition(ex, request);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    void handleProductInactive() {
        ProductInactiveException ex = new ProductInactiveException("Product is inactive");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleProductInactive(ex, request);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    void handleValidationExceptions() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "field1", "Field1 is required"));
        bindingResult.addError(new FieldError("target", "field2", "Field2 is invalid"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                (MethodParameter) null, bindingResult
        );

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Field1 is required", response.getBody().get("field1"));
        assertEquals("Field2 is invalid", response.getBody().get("field2"));
    }

    @Test
    void handleGenericException() {
        Exception ex = new RuntimeException("Something broke");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        assertErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
}
