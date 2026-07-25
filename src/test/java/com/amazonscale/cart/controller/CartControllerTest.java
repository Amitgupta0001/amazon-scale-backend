package com.amazonscale.cart.controller;

import com.amazonscale.cart.dto.AddToCartRequest;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.dto.UpdateCartItemRequest;
import com.amazonscale.cart.entity.CurrencyCode;
import com.amazonscale.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;

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
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return 1L; // Mock user ID 1L
                    }
                })
                .build();

        sampleCartResponse = CartResponse.builder()
                .cartId(100L)
                .userId(1L)
                .items(Collections.emptyList())
                .totalItems(0)
                .totalAmount(BigDecimal.ZERO)
                .currency(CurrencyCode.INR)
                .build();
    }

    @Test
    void testAddItemToCartSuccess() throws Exception {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(10L)
                .quantity(2)
                .build();

        when(cartService.addItemToCart(eq(1L), any(AddToCartRequest.class))).thenReturn(sampleCartResponse);

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartId").value(100L))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(cartService, times(1)).addItemToCart(eq(1L), any(AddToCartRequest.class));
    }

    @Test
    void testAddItemToCartValidationError() throws Exception {
        AddToCartRequest request = new AddToCartRequest(); // missing productId and quantity

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).addItemToCart(any(), any());
    }

    @Test
    void testGetCartSuccess() throws Exception {
        when(cartService.getCart(1L)).thenReturn(sampleCartResponse);

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(100L));

        verify(cartService, times(1)).getCart(1L);
    }

    @Test
    void testUpdateCartItemSuccess() throws Exception {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(5)
                .build();

        when(cartService.updateCartItem(eq(1L), eq(10L), any(UpdateCartItemRequest.class))).thenReturn(sampleCartResponse);

        mockMvc.perform(put("/api/v1/cart/items/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(100L));

        verify(cartService, times(1)).updateCartItem(eq(1L), eq(10L), any(UpdateCartItemRequest.class));
    }

    @Test
    void testRemoveItemFromCartSuccess() throws Exception {
        doNothing().when(cartService).removeCartItem(1L, 10L);

        mockMvc.perform(delete("/api/v1/cart/items/10"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).removeCartItem(1L, 10L);
    }

    @Test
    void testClearCartSuccess() throws Exception {
        doNothing().when(cartService).clearCart(1L);

        mockMvc.perform(delete("/api/v1/cart"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).clearCart(1L);
    }
}
