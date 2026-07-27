package com.amazonscale.cart.controller;

import com.amazonscale.cart.dto.AddToCartRequest;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.dto.UpdateCartItemRequest;
import com.amazonscale.cart.entity.CurrencyCode;
import com.amazonscale.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private ObjectMapper objectMapper;
    private CartResponse sampleCartResponse;

    @BeforeEach
    void setUp() {
        HandlerMethodArgumentResolver authenticationPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                return 1L;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setCustomArgumentResolvers(authenticationPrincipalResolver)
                .build();

        objectMapper = new ObjectMapper();

        sampleCartResponse = CartResponse.builder()
                .cartId(100L)
                .userId(1L)
                .items(List.of())
                .totalItems(0)
                .totalAmount(BigDecimal.ZERO)
                .currency(CurrencyCode.INR)
                .build();
    }

    @Test
    @DisplayName("Should add item to cart successfully and return HTTP 201 Created")
    void shouldAddItemToCartSuccessfully() throws Exception {
        // Arrange
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(10L)
                .quantity(2)
                .build();

        when(cartService.addItemToCart(eq(1L), any(AddToCartRequest.class))).thenReturn(sampleCartResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartId").value(100L))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(cartService, times(1)).addItemToCart(eq(1L), any(AddToCartRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when AddToCartRequest fails Bean Validation")
    void shouldReturnBadRequestWhenAddToCartValidationFails() throws Exception {
        // Arrange
        AddToCartRequest invalidRequest = new AddToCartRequest(); // null productId & quantity

        // Act & Assert
        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).addItemToCart(any(), any());
    }

    @Test
    @DisplayName("Should get user cart and return HTTP 200 OK")
    void shouldGetCartSuccessfully() throws Exception {
        // Arrange
        when(cartService.getCart(1L)).thenReturn(sampleCartResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(100L));

        verify(cartService, times(1)).getCart(1L);
    }

    @Test
    @DisplayName("Should update cart item quantity and return HTTP 200 OK")
    void shouldUpdateCartItemSuccessfully() throws Exception {
        // Arrange
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(5)
                .build();

        when(cartService.updateCartItem(eq(1L), eq(10L), any(UpdateCartItemRequest.class)))
                .thenReturn(sampleCartResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/cart/items/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(100L));

        verify(cartService, times(1)).updateCartItem(eq(1L), eq(10L), any(UpdateCartItemRequest.class));
    }

    @Test
    @DisplayName("Should remove item from cart and return HTTP 204 No Content")
    void shouldRemoveItemFromCartSuccessfully() throws Exception {
        // Arrange
        doNothing().when(cartService).removeCartItem(1L, 10L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/cart/items/10"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).removeCartItem(1L, 10L);
    }

    @Test
    @DisplayName("Should clear cart and return HTTP 204 No Content")
    void shouldClearCartSuccessfully() throws Exception {
        // Arrange
        doNothing().when(cartService).clearCart(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/cart"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).clearCart(1L);
    }
}
