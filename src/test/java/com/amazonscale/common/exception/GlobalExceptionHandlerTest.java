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
import com.amazonscale.payment.exception.InvalidPaymentException;
import com.amazonscale.payment.exception.PaymentFailedException;
import com.amazonscale.payment.exception.PaymentNotFoundException;
import com.amazonscale.product.exception.ProductInactiveException;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.user.exception.EmailAlreadyExistsException;
import com.amazonscale.wishlists.exception.DefaultWishlistModificationException;
import com.amazonscale.wishlists.exception.WishlistAlreadyExistsException;
import com.amazonscale.wishlists.exception.WishlistItemAlreadyExistsException;
import com.amazonscale.wishlists.exception.WishlistItemNotFoundException;
import com.amazonscale.wishlists.exception.WishlistNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(expectedStatus.value());
        assertThat(response.getBody().getError()).isEqualTo(expectedStatus.getReasonPhrase());
        assertThat(response.getBody().getMessage()).isEqualTo(expectedMessage);
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle ProductNotFoundException and return 404 NOT FOUND")
    void shouldHandleProductNotFoundException() {
        // Arrange
        ProductNotFoundException ex = new ProductNotFoundException(1L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleProductNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return 400 BAD REQUEST with field errors")
    void shouldHandleMethodArgumentNotValidException() {
        // Arrange
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "email", "Email is required"));
        bindingResult.addError(new FieldError("target", "price", "Price must be positive"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                (MethodParameter) null, bindingResult
        );

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .isNotNull()
                .containsEntry("email", "Email is required")
                .containsEntry("price", "Price must be positive");
    }

    @Test
    @DisplayName("Should handle EmailAlreadyExistsException and return 409 CONFLICT")
    void shouldHandleEmailAlreadyExistsException() {
        // Arrange
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("user@example.com");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmailAlreadyExists(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle CategoryNotFoundException and return 404 NOT FOUND")
    void shouldHandleCategoryNotFoundException() {
        // Arrange
        CategoryNotFoundException ex = new CategoryNotFoundException(5L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle CategoryAlreadyExistsException and return 409 CONFLICT")
    void shouldHandleCategoryAlreadyExistsException() {
        // Arrange
        CategoryAlreadyExistsException ex = new CategoryAlreadyExistsException("Electronics");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryAlreadyExists(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidCategoryHierarchyException and return 400 BAD REQUEST")
    void shouldHandleInvalidCategoryHierarchyException() {
        // Arrange
        InvalidCategoryHierarchyException ex = new InvalidCategoryHierarchyException();

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryHierarchy(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle InventoryNotFoundException and return 404 NOT FOUND")
    void shouldHandleInventoryNotFoundException() {
        // Arrange
        InventoryNotFoundException ex = new InventoryNotFoundException(10L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInventoryNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle InventoryAlreadyExistsException and return 409 CONFLICT")
    void shouldHandleInventoryAlreadyExistsException() {
        // Arrange
        InventoryAlreadyExistsException ex = new InventoryAlreadyExistsException(12L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInventoryAlreadyExists(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle InsufficientStockException and return 400 BAD REQUEST")
    void shouldHandleInsufficientStockException() {
        // Arrange
        InsufficientStockException ex = new InsufficientStockException("Insufficient stock");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInsufficientStock(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle CartNotFoundException and return 404 NOT FOUND")
    void shouldHandleCartNotFoundException() {
        // Arrange
        CartNotFoundException ex = new CartNotFoundException(50L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCartNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle CartItemNotFoundException and return 404 NOT FOUND")
    void shouldHandleCartItemNotFoundException() {
        // Arrange
        CartItemNotFoundException ex = new CartItemNotFoundException(20L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCartItemNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidQuantityException and return 400 BAD REQUEST")
    void shouldHandleInvalidQuantityException() {
        // Arrange
        InvalidQuantityException ex = new InvalidQuantityException("Quantity must be greater than 0");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidQuantity(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle OrderNotFoundException and return 404 NOT FOUND")
    void shouldHandleOrderNotFoundException() {
        // Arrange
        OrderNotFoundException ex = new OrderNotFoundException(100L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle EmptyCartException and return 400 BAD REQUEST")
    void shouldHandleEmptyCartException() {
        // Arrange
        EmptyCartException ex = new EmptyCartException("Cannot place order with empty cart");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmptyCart(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidOrderStatusTransitionException and return 400 BAD REQUEST")
    void shouldHandleInvalidOrderStatusTransitionException() {
        // Arrange
        InvalidOrderStatusTransitionException ex = new InvalidOrderStatusTransitionException("Invalid transition");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidOrderStatusTransition(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle ProductInactiveException and return 400 BAD REQUEST")
    void shouldHandleProductInactiveException() {
        // Arrange
        ProductInactiveException ex = new ProductInactiveException("Product is inactive");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleProductInactive(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500 INTERNAL SERVER ERROR")
    void shouldHandleGenericException() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected internal server error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    @Test
    @DisplayName("Should handle PaymentNotFoundException and return 404 NOT FOUND")
    void shouldHandlePaymentNotFoundException() {
        // Arrange
        PaymentNotFoundException ex = new PaymentNotFoundException("Payment not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handlePaymentNotFoundException(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, "Payment not found");
    }

    @Test
    @DisplayName("Should handle InvalidPaymentException and return 400 BAD REQUEST")
    void shouldHandleInvalidPaymentException() {
        // Arrange
        InvalidPaymentException ex = new InvalidPaymentException("Invalid payment state");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidPaymentException(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid payment state");
    }

    @Test
    @DisplayName("Should handle PaymentFailedException and return 402 PAYMENT REQUIRED")
    void shouldHandlePaymentFailedException() {
        // Arrange
        PaymentFailedException ex = new PaymentFailedException("Payment failed");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handlePaymentFailedException(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.PAYMENT_REQUIRED, "Payment failed");
    }

    @Test
    @DisplayName("Should handle WishlistNotFoundException and return 404 NOT FOUND")
    void shouldHandleWishlistNotFoundException() {
        // Arrange
        WishlistNotFoundException ex = new WishlistNotFoundException("Wishlist not found with id 1");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWishlistNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, "Wishlist not found with id 1");
    }

    @Test
    @DisplayName("Should handle WishlistAlreadyExistsException and return 409 CONFLICT")
    void shouldHandleWishlistAlreadyExistsException() {
        // Arrange
        WishlistAlreadyExistsException ex = new WishlistAlreadyExistsException("Wishlist already exists");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWishlistAlreadyExist(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.CONFLICT, "Wishlist already exists");
    }

    @Test
    @DisplayName("Should handle WishlistItemAlreadyExistsException and return 409 CONFLICT")
    void shouldHandleWishlistItemAlreadyExistsException() {
        // Arrange
        WishlistItemAlreadyExistsException ex = new WishlistItemAlreadyExistsException("Item already in wishlist");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWishlistItemAlreadyExist(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.CONFLICT, "Item already in wishlist");
    }

    @Test
    @DisplayName("Should handle WishlistItemNotFoundException and return 404 NOT FOUND")
    void shouldHandleWishlistItemNotFoundException() {
        // Arrange
        WishlistItemNotFoundException ex = new WishlistItemNotFoundException("Wishlist item not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWishlistItemNotFound(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.NOT_FOUND, "Wishlist item not found");
    }

    @Test
    @DisplayName("Should handle DefaultWishlistModificationException and return 400 BAD REQUEST")
    void shouldHandleDefaultWishlistModificationException() {
        // Arrange
        DefaultWishlistModificationException ex = new DefaultWishlistModificationException("Cannot delete default wishlist");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDefaultWishlistModificationException(ex, request);

        // Assert
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Cannot delete default wishlist");
    }
}
